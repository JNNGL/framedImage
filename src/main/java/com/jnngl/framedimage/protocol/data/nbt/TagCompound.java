/*
 *  Copyright (C) 2022-2026  JNNGL
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jnngl.framedimage.protocol.data.nbt;

import io.netty.buffer.ByteBuf;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TagCompound extends NbtTag implements Iterable<NbtTag> {

  private Map<String, NbtTag> value;

  public TagCompound(String name, Set<NbtTag> value) {
    super(name);
    setValue(value);
  }

  public TagCompound(String name) {
    super(name);
  }

  @Override
  public int getTypeID() {
    return 10;
  }

  @Override
  public void encode(ByteBuf buf) {
    if (value != null) {
      value.values().forEach(tag -> Nbt.write(buf, tag, false));
    }

    buf.writeByte(0);
  }

  public Map<String, NbtTag> getValue() {
    return value;
  }

  public NbtTag get(String key) {
    return value.get(key);
  }

  public void setValue(Set<NbtTag> value) {
    this.value = value.stream().collect(Collectors.toMap(NbtTag::getName, Function.identity()));
  }

  public void set(NbtTag tag) {
    value.put(tag.getName(), tag);
  }

  @Override
  public String toString() {
    String[] values = value.values().stream().map(NbtTag::toString).toArray(String[]::new);
    String valueString = String.join("\n", values);
    String withName = "Compound('" + getName() + "'): \n" + valueString;
    return withName.replace("\n", "\n  ");
  }

  @Override
  public Iterator<NbtTag> iterator() {
    return value.values().iterator();
  }
}
