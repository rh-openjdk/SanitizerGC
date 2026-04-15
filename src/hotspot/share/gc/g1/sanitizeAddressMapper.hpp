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

#ifndef SHARE_GC_G1_SANITIZEADDRESSMAPPER_HPP
#define SHARE_GC_G1_SANITIZEADDRESSMAPPER_HPP

class SanitizeGCMapper {
public:

    static const void* mapNewAddrToOriginalAddrImpl(const void* newAddr);
    static const void* mapNewEdgeAddrToOriginalAddrImpl(const void* newAddr);
    static const void* mapOriginalAddrToNewAddrImpl(const void *newAddr);
    static const void* mapOriginalEdgeAddrToNewAddrImpl(const void* newAddr);

    template <typename T> static inline void mapNewAddrToOriginalAddr(T &addr) {
      addr = (T) mapNewAddrToOriginalAddrImpl(addr);
    }
    template <typename T> static inline void mapNewEdgeAddrToOriginalAddr(T &addr) {
      addr = (T) mapNewEdgeAddrToOriginalAddrImpl(addr);
    }
    template <typename T> static inline void mapOriginalAddrToNewAddr(T &addr) {
      addr = (T) mapOriginalAddrToNewAddrImpl(addr);
    }
    template <typename T> static inline void mapOriginalEdgeAddrToNewAddr(T &addr) {
      addr = (T) mapOriginalEdgeAddrToNewAddrImpl(addr);
    }
};

#define MacroAssemblerMapNewAddrToOriginalAddr(addr) do {                                               \
  if (SanitizeGC) {                                                                                     \
    RegSet exclude_set = RegSet::of((addr));                                                            \
    __ push_call_clobbered_registers_except(exclude_set);                                               \
    __ call_VM_leaf(CAST_FROM_FN_PTR(address, SanitizeGCMapper::mapNewAddrToOriginalAddrImpl), (addr)); \
    __ movptr((addr), rax);                                                                             \
    __ pop_call_clobbered_registers_except(exclude_set);                                                \
  }                                                                                                     \
} while(0)

#endif // SHARE_GC_G1_SANITIZEADDRESSMAPPER_HPP
