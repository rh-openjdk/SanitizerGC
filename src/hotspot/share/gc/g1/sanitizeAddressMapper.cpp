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

#include "sanitizeAddressMapper.hpp"
#include "sanitizeGCHeapRegionMap.hpp"

const void* SanitizeGCMapper::mapNewAddrToOriginalAddrImpl(const void* newAddr) {
    if (SanitizeGCRegionMaps::are_initialized) {
        const void* remapped = SanitizeGCRegionMaps::moved_to_original->remap_address(newAddr);
        if (remapped != nullptr) {
            return remapped;
        }
    }

    return newAddr;
}

const void* SanitizeGCMapper::mapNewEdgeAddrToOriginalAddrImpl(const void* newAddr) {
    if (SanitizeGCRegionMaps::are_initialized) {
        const void* remapped = SanitizeGCRegionMaps::moved_to_original->remap_end_address(newAddr);
        if (remapped != nullptr) {
            return remapped;
        }
    }

    return newAddr;
}

const void* SanitizeGCMapper::mapOriginalAddrToNewAddrImpl(const void* originalAddr) {
    if (SanitizeGCRegionMaps::are_initialized) {
        const void* remapped = SanitizeGCRegionMaps::original_to_moved->remap_address(originalAddr);
        if (remapped != nullptr) {
            return remapped;
        }
    }

    return originalAddr;
}

const void* SanitizeGCMapper::mapOriginalEdgeAddrToNewAddrImpl(const void* newAddr) {
    if (SanitizeGCRegionMaps::are_initialized) {
        const void* remapped = SanitizeGCRegionMaps::original_to_moved->remap_end_address(newAddr);
        if (remapped != nullptr) {
            return remapped;
        }
    }

    return newAddr;
}
