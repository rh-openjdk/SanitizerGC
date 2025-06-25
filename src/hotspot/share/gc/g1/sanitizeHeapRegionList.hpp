#ifndef SHARE_GC_G1_SANITIZEHEAPREGIONMAP_HPP
#define SHARE_GC_G1_SANITIZEHEAPREGIONMAP_HPP

#include <cstddef>

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

#endif // SHARE_GC_G1_SANITIZEHEAPREGIONMAP_HPP