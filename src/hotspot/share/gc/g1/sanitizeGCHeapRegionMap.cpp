#include "sanitizeGCHeapRegionMap.hpp"

bool SanitizeGCRegionMaps::are_initialized = false;
RegionMap *SanitizeGCRegionMaps::moved_to_original = nullptr;
RegionMap *SanitizeGCRegionMaps::original_to_moved = nullptr;
unsigned int RegionMapEntry::shift_by = 0;
