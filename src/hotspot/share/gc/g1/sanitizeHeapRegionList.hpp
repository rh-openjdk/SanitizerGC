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

class SanitizeGCConsts {
public:
  static unsigned int mask_size;
};

class RegionMapConfig {
public:
  using Key = const void*;
  using Value = const void*;

  static uintx get_hash(Key const& key, bool* dead) {
    return (uintx)key >> SanitizeGCConsts::mask_size;
  }

  static void* allocate_node(void* context, size_t size, Value const& value) {
    return AllocateHeap(sizeof(const void *), mtGC);
  }

  static void free_node(void* context, void* memory, Value const& value) {
    FreeHeap(memory);
  }
};

class RegionMap : public CHeapObj<mtGC> {
  using HashTable = ConcurrentHashTable<RegionMapConfig, mtGC>;

  HashTable _table;

  size_t volatile _num_entries;

  class RegionMapLookUp {
    const void* _address;
  public:
    explicit RegionMapLookUp(const void * address) : _address(address) { }
    uintx get_hash() const {
      return (uintx)_address >> SanitizeGCConsts::mask_size;
    }
    bool equals(const void** value) {
      // return _address == *value;
      return true;
    }
    bool is_dead(const void** value) const { return false; }
  };

  class RegionMapGet {
    const void* _return;
  public:
    RegionMapGet() : _return(nullptr) {}
    void operator()(const void** value) {
      assert(value != nullptr, "expected valid value");
      _return = *value;
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
           false),
    _num_entries(0) { }

  void insert(const void* key, const void* value) {
    RegionMapLookUp lookup(key);
    bool grow_hint = false;
    bool inserted = _table.insert(Thread::current(), lookup, value, &grow_hint);
    if (inserted) {
      Atomic::inc(&_num_entries);
    }
    if (grow_hint) {
      _table.grow(Thread::current());
    }
  }

  bool remove(const void* key) {
    RegionMapLookUp lookup(key);
    bool removed = _table.remove(Thread::current(), lookup);
    if (removed) {
      Atomic::dec(&_num_entries);
    }
    return removed;
  }

  const void* get(const void* key) {
    RegionMapLookUp lookup(key);
    RegionMapGet rmg;
    bool found = _table.get(Thread::current(), lookup, rmg);
    if (found) {
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