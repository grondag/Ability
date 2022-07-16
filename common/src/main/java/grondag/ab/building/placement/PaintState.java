/*
 * This file is part of Ability and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package grondag.ab.building.placement;

import java.util.function.BiFunction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import grondag.ab.building.block.base.FormedBlockMaterial;
import grondag.ab.building.block.init.FormedBlockMaterials;
import grondag.xm.api.modelstate.primitive.MutablePrimitiveState;
import grondag.xm.api.paint.PaintIndex;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.primitive.surface.SurfaceLocation;
import grondag.xm.api.util.ColorUtil;
import grondag.xm.util.SimpleEnumCodec;

public class PaintState {
	private FormedBlockMaterial material = FormedBlockMaterials.DURACRETE;
	private XmPaint[] raw = new XmPaint[SurfaceLocation.COUNT];
	private XmPaint[] cached = new XmPaint[SurfaceLocation.COUNT];

	private PaintSupplier[] suppliers = new PaintSupplier[SurfaceLocation.COUNT];

	private enum PaintSupplier {
		MATERIAL((state, location)-> state.material.paint()),
		RAW((state, location)-> state.raw[location.ordinal()]),
		OUTSIDE((state, location)-> state.forLocation(SurfaceLocation.OUTSIDE)),
		INSIDE(shade(SurfaceLocation.OUTSIDE, 0.85f)),
		CUT(shade(SurfaceLocation.OUTSIDE, 0.92f));

		private final BiFunction<PaintState, SurfaceLocation, XmPaint> function;

		PaintSupplier(BiFunction<PaintState, SurfaceLocation, XmPaint> function) {
			this.function = function;
		}

		private static final SimpleEnumCodec<PaintSupplier> CODEC = new SimpleEnumCodec<>(PaintSupplier.class);
	}

	private static final int OUTISDE_INDEX = SurfaceLocation.OUTSIDE.ordinal();

	private static BiFunction<PaintState, SurfaceLocation, XmPaint> shade(SurfaceLocation sourceLocation, float shade) {
		return (state, location) -> {
			final XmPaint source = state.forLocation(sourceLocation);
			return XmPaint.finder().copy(source).textureColor(0, ColorUtil.multiplyRGB(source.textureColor(0), shade)).find();
		};
	}

	public PaintState() {
		clear();
	}

	public CompoundTag toTag() {
		final var result = new CompoundTag();

		result.putString("mat", material.code());

		for (int i = 0; i < SurfaceLocation.COUNT; ++i) {
			result.put("p" + i, raw[i].toTag());
			result.putString("s" + i, suppliers[i].name());
		}

		return result;
	}

	public void fromTag(CompoundTag tag, Level level) {
		clear();

		if (tag != null) {
			material = FormedBlockMaterial.get(tag.getString("mat"));

			for (int i = 0; i < SurfaceLocation.COUNT; ++i) {
				raw[i] = XmPaint.fromTag(tag.getCompound("p" + i), PaintIndex.forWorld(level));
				suppliers[i] = PaintSupplier.CODEC.fromName(tag.getString("s" + i));
			}
		}
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeUtf(material.code());

		for (int i = 0; i < SurfaceLocation.COUNT; ++i) {
			raw[i].toBytes(buf);
			buf.writeVarInt(suppliers[i].ordinal());
		}
	}

	public void fromBytes(FriendlyByteBuf buf, Level level) {
		clear();
		material = FormedBlockMaterial.get(buf.readUtf());

		for (int i = 0; i < SurfaceLocation.COUNT; ++i) {
			raw[i] = XmPaint.fromBytes(buf, PaintIndex.forWorld(level));
			suppliers[i] = PaintSupplier.CODEC.fromOrdinal(buf.readVarInt());
		}
	}

	public void clear() {
		clearCache();
		final var defaultPaint = FormedBlockMaterials.DURACRETE.paint();

		for (int i = 0; i < SurfaceLocation.COUNT; ++i) {
			// raw isn't used unless set but making non-null simplifies serialization
			raw[i] = defaultPaint;
			suppliers[i] = PaintSupplier.OUTSIDE;
		}

		suppliers[OUTISDE_INDEX] = PaintSupplier.MATERIAL;
		suppliers[SurfaceLocation.INSIDE.ordinal()] = PaintSupplier.INSIDE;
		suppliers[SurfaceLocation.CUT.ordinal()] = PaintSupplier.CUT;
	}

	public void setRaw(SurfaceLocation location, XmPaint paint) {
		final var index = location.ordinal();
		raw[index] = paint;
		suppliers[index] = PaintSupplier.RAW;
		clearCache();
	}

	public void switchMaterial(FormedBlockMaterial material) {
		if (material != this.material) {
			this.material = material;
			clearCache();
		}
	}

	private void clearCache() {
		for (int i = 0; i < SurfaceLocation.COUNT; ++i) {
			cached[i] = null;
		}
	}

	public XmPaint forLocation(SurfaceLocation location) {
		final var index = location.ordinal();

		var result = cached[index];

		if (result == null) {
			result = suppliers[index].function.apply(this, location);
			cached[index] = result;
		}

		return result;
	}

	public void applyToState(MutablePrimitiveState state) {
		final var surfaces = state.primitive().surfaces(state);

		for (int i = 0; i < surfaces.size(); ++i) {
			state.paint(i, forLocation(surfaces.get(i).location()));
		}
	}
}
