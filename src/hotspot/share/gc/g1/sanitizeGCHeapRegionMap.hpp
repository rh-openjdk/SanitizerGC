/*
* Copyright (c) 2026, IBM.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#ifndef SHARE_GC_G1_SANITIZEHEAPREGIONMAP_HPP
#define SHARE_GC_G1_SANITIZEHEAPREGIONMAP_HPP

#include <cstddef>
#include "memory/allocation.hpp"
#include "utilities/concurrentHashTable.hpp"
#include "utilities/concurrentHashTableTasks.inline.hpp"

class RegionMapEntry {
public:
  const void* key;
  const void* value;

  static unsigned int shift_by;
  static uintx shift_address(const void* address) {
    return (uintx)address >> shift_by;
  }
};

class RegionMapConfig {
public:
  using Value = RegionMapEntry;

  static uintx get_hash(Value const& map_entry, bool* dead) {
    return RegionMapEntry::shift_address(map_entry.key);
  }

  static void* allocate_node(void* context, size_t size, Value const& map_entry) {
    return AllocateHeap(size, mtGC);
  }

  static void free_node(void* context, void* memory, Value const& map_entry) {
    FreeHeap(memory);
  }
};

class RegionMap : public CHeapObj<mtGC> {
  using HashTable = ConcurrentHashTable<RegionMapConfig, mtGC>;

  HashTable _table;
  uint _mask_size;

  class RegionMapLookUp {
    const void* _address;
  public:
    explicit RegionMapLookUp(const void* address) : _address(address) { }
    uintx get_hash() const {
      return RegionMapEntry::shift_address(_address);
    }
    bool equals(RegionMapEntry* map_entry) {
      return (uintx)map_entry->key >> RegionMapEntry::shift_by == (uintx)_address >> RegionMapEntry::shift_by;
    }
    bool is_dead(RegionMapEntry* value) const { return false; }
  };

  class RegionMapGet {
    const void* _return;
  public:
    RegionMapGet() : _return(nullptr) {}
    void operator()(RegionMapEntry* map_entry) {
      assert(map_entry != nullptr, "SanitizeGC: Expected valid value.");
      _return = map_entry->value;
    }
    const void* get_entry_value() const {
      return _return;
    }
  };

public:
  RegionMap() :
    _table(Mutex::service-1,
           nullptr,
           16,
           false) { }

  bool insert(const void* key, const void* value) {
    RegionMapEntry map_entry = {key, value};
    RegionMapLookUp lookup(key);
    bool grow_hint = false;
    RegionMapGet rmg;

    if (_table.get(Thread::current(), lookup, rmg)) {
      // an entry with this key is already present in the table, returning false
      return false;
    }

    bool inserted = _table.insert(Thread::current(), lookup, map_entry, &grow_hint);
    if (grow_hint) {
      _table.grow(Thread::current());
    }
    return inserted;
  }

  bool update(const void* key, const void* new_value) {
    RegionMapEntry new_map_entry = {key, new_value};
    RegionMapLookUp lookup(key);
    bool grow_hint = false;
    RegionMapGet rmg;

    // remove the old entry
    bool found = _table.get(Thread::current(), lookup, rmg);
    if (found) {
      if (!_table.remove(Thread::current(), lookup)) {
        // failed to remove the old entry
        return false;
      }
    }

    bool inserted = _table.insert(Thread::current(), lookup, new_map_entry, &grow_hint);
    if (grow_hint) {
      _table.grow(Thread::current());
    }
    return inserted;
  }

  bool remove(const void* key) {
    RegionMapLookUp lookup(key);
    return _table.remove(Thread::current(), lookup);
  }

  const void* remap_address(const void* addr) {
    if (SanitizeGC) {
      RegionMapLookUp lookup(addr);
      RegionMapGet rmg;

      bool found = _table.get(Thread::current(), lookup, rmg);
      if (found) {
        // remap the given address to the one which was found
        const uintptr_t mask_bits = ((uintptr_t)1 << RegionMapEntry::shift_by) - 1;
        const uintptr_t low_bits_to_add = (uintptr_t)addr & mask_bits;

        return (const void*)((uintptr_t)rmg.get_entry_value() | low_bits_to_add);
      }
    }

    // no matching entry was found
    return nullptr;
  }

  const void* remap_end_address(const void* addr) {
    const void* result = nullptr;
    if (SanitizeGC) {
      uintptr_t converted_addr = (uintptr_t)addr;
      const void* remapped = remap_address((const void*)(converted_addr - 1));
      if (remapped != nullptr) {
        result = (const void*)((uintptr_t)remapped + 1);
      }
    }

    return result;
  }
};

class SanitizeGCRegionMaps {
public:
  static RegionMap* moved_to_original;
  static RegionMap* original_to_moved;
  static bool are_initialized;
};

#endif // SHARE_GC_G1_SANITIZEHEAPREGIONMAP_HPP