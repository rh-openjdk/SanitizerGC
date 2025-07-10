#include "sanitizeHeapRegionList.hpp"

#include <cstdio>

// Since we can't use void* for pointer arithmetic, we need another pointer
// type, whose base element size is the unit for movedRegionOffset. We must use
// the same pointer type when computing movedRegionOffset as when applying it,
// so we define it here to ensure it is the same at both places. We choose char*
// which makes movedRegionOffset unit to be bytes.
using byte_ptr = const char*;

region_info_t SanitizeGCHeapRegionList::regions[150]; // TODO, random number for now
int SanitizeGCHeapRegionList::arrayPosition = 0;

bool SanitizeGCRegionMaps::are_initialized = false;
RegionMap *SanitizeGCRegionMaps::moved_map = nullptr;
RegionMap *SanitizeGCRegionMaps::original_map = nullptr;

void SanitizeGCHeapRegionList::add_region_to_list(const void* original_region_start, const void* original_region_end,
                                const void* moved_region_start, const void* moved_region_end) {
  region_info_t region_info;
  region_info.offset = static_cast<byte_ptr>(original_region_start) - static_cast<byte_ptr>(moved_region_start);
  region_info.original_start = original_region_start;
  region_info.original_end = original_region_end;
  region_info.moved_start = moved_region_start;
  region_info.moved_end = moved_region_end;

  regions[arrayPosition++] = region_info;
}

region_info_t SanitizeGCHeapRegionList::get_region_from_list(const void* address, bool moved) {
  region_info_t region_info;

  region_info.offset = 0;

  if (moved) {
    for (int i = 0; i < arrayPosition; i++) {
      if (regions[i].moved_start <= address && regions[i].moved_end >= address) {
        region_info = regions[i];
        break;
      }
    }
  } else {
    for (int i = 0; i < arrayPosition; i++) {
      if (regions[i].original_start <= address && regions[i].original_end >= address) {
        region_info = regions[i];
        break;
      }
    }
  }

  return region_info;
}
