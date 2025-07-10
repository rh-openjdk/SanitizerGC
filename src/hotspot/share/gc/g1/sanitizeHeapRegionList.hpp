#ifndef SHARE_GC_G1_SANITIZEHEAPREGIONMAP_HPP
#define SHARE_GC_G1_SANITIZEHEAPREGIONMAP_HPP

#include <cstddef>
#include <utilities/concurrentHashTable.hpp>
#include <utilities/concurrentHashTableTasks.inline.hpp>
#include "memory/allocation.hpp"

struct region_info_t {
  ptrdiff_t offset;
  const void* original_start;
  const void* original_end;
  const void* moved_start;
  const void* moved_end;
};

class SanitizeGCHeapRegionList {
private:
  static region_info_t regions[150];
  static int arrayPosition;

public:
  static void add_region_to_list(const void* original_region_start, const void* original_region_end,
                                const void* moved_region_start, const void* moved_region_end);
  static region_info_t get_region_from_list(const void* address, bool moved);
  // void update_region_in_maps(...); TODO
  // void delete_region_in_maps(...); TODO
};


class RegionMapConfig {
public:
  using Key = const void*;
  using Value = const void*;

  static uintx get_hash(Key const& key, bool* dead) {
    // TODO mask the address instead
    return (uintx)key >> 23;
  }

  static void* allocate_node(void* context, size_t size, Value const& value) {
    return AllocateHeap(size + sizeof(const void *), mtGC);
  }

  static void free_node(void* context, void* memory, Value const& value) {
    FreeHeap(memory);
  }
};

class RegionMap : public CHeapObj<mtGC> {
  using HashTable = ConcurrentHashTable<RegionMapConfig, mtGC>;

  HashTable _table;

  size_t volatile _num_entries;

  bool is_empty() const { return number_of_entries() == 0; }

  class RegionMapLookUp : public StackObj {
    const void * _address;
  public:
    explicit RegionMapLookUp(const void * address) : _address(address) { }
    uintx get_hash() const {
      // TODO mask the address instead
      return (uintx)_address >> 23;
    }
    bool equals(const void** value) {
      return false; // TODO, needs better logic!
    }
    bool is_dead(const void** value) const { return false; }
  };

  class RegionMapGet : public StackObj {
    const void* _return;
  public:
    RegionMapGet() : _return(nullptr) {}
    void operator()(const void** value) {
      assert(value != nullptr, "expected valid value");
      _return = &value;
    }
    const void* get_region_info() const {
      return _return;
    }
  };

public:
  RegionMap() :
    _table(Mutex::service-1,
           nullptr,
           16,
           false /* enable_statistics */),
    _num_entries(0) { }

  static uintx get_hash(const void* ri) {
    return (uintx)ri >> 23;
  }

  void insert(const void* address, const void* region) {
    RegionMapLookUp lookup(address);
    bool grow_hint = false;
    bool inserted = _table.insert(Thread::current(), lookup, region, &grow_hint);
    if (inserted) {
      Atomic::inc(&_num_entries);
    }
    if (grow_hint) {
      _table.grow(Thread::current());
    }
  }

  bool remove(const void* address) {
    RegionMapLookUp lookup(address);
    bool removed = _table.remove(Thread::current(), lookup);
    if (removed) {
      Atomic::dec(&_num_entries);
    }
    return removed;
  }

  const void* get(const void* address) {
    RegionMapLookUp lookup(address);
    RegionMapGet rmg;
    bool got = _table.get(Thread::current(), lookup, rmg);
    if (got) {
      return rmg.get_region_info();
    }
    return nullptr;
  }

  size_t number_of_entries() const { return Atomic::load(&_num_entries); }
};

class SanitizeGCRegionMaps {
public:
  static RegionMap* moved_map;
  static RegionMap* original_map;
  static bool are_initialized;
};

#endif // SHARE_GC_G1_SANITIZEHEAPREGIONMAP_HPP