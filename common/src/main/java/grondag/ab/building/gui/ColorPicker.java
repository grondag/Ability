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

package grondag.ab.building.gui;

import static grondag.ab.ux.client.color.BlockColors.ARC_DEGREES;
import static grondag.ab.ux.client.color.BlockColors.CHROMA_SLICE_COUNT;
import static grondag.ab.ux.client.color.BlockColors.CHROMA_SLICE_SIZE;
import static grondag.ab.ux.client.color.BlockColors.DEFAULT_WHITE_HCL;
import static grondag.ab.ux.client.color.BlockColors.DEFAULT_WHITE_RGB;
import static grondag.ab.ux.client.color.BlockColors.HCL_TO_RGB;
import static grondag.ab.ux.client.color.BlockColors.HUE_COLORS;
import static grondag.ab.ux.client.color.BlockColors.LUMA_SLICE_COUNT;
import static grondag.ab.ux.client.color.BlockColors.LUMA_SLICE_SIZE;
import static grondag.ab.ux.client.color.BlockColors.RGB_TO_HCL;
import static grondag.ab.ux.client.color.ColorUtil.NO_COLOR;

import it.unimi.dsi.fastutil.ints.IntConsumer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.GameRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.ab.ux.client.ScreenTheme;
import grondag.ab.ux.client.control.AbstractControl;

@Environment(EnvType.CLIENT)
public class ColorPicker extends AbstractControl<ColorPicker> {
	/**
	 * Hue chosen by user, or set at init - will match currrent rbg
	 */
	private int hue = DEFAULT_WHITE_HCL & 0xFFFF;

	/**
	 * Chroma last chosen by user, may not match current rgb
	 */
	private int chroma =  (DEFAULT_WHITE_HCL >> 16) & 0xFF;

	private int effectiveChroma = chroma;

	/**
	 * Luminance last chosen by user, will match current rgb
	 */
	private int luminance = (DEFAULT_WHITE_HCL >> 24) & 0xFF;

	private int rgb = DEFAULT_WHITE_RGB;

	private IntConsumer onRgbChange = i -> {};

	private final int[][] mix = new int[LUMA_SLICE_COUNT][CHROMA_SLICE_COUNT];

	public int getRgb() {
		return rgb;
	}

	public void setRgb(int rgb) {
		if (rgb != this.rgb) {
			this.rgb = rgb;
			final int hcl = RGB_TO_HCL.get(rgb);

			luminance = (hcl >> 24) & 0xFF;
			chroma = (hcl >> 16) & 0xFF;
			effectiveChroma = chroma;
			changeHueIfDifferent(hcl & 0xFFFF);
		}
	}

	public void onChange(IntConsumer onRgbChange) {
		this.onRgbChange = onRgbChange;
	}

	public ColorPicker(ScreenTheme theme) {
		super(theme);
		aspectRatio(1.0f);
		rebuildMix();
	}

	@Override
	protected void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		final BufferBuilder vertexbuffer = Tesselator.getInstance().getBuilder();
		vertexbuffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

		final float xStep = width * ARC_DEGREES / 360F;
		float x0 = left;
		float x1 = x0 + xStep;
		final float y0 = top + 10;
		final float y1 = top;

		int hIndex = 0;
		int c0 = HUE_COLORS[hIndex];
		int c1;

		for (int h = 0; h < 360; h += ARC_DEGREES) {
			c1 = HUE_COLORS[++hIndex];
			vertexbuffer.vertex(x0, y0, 0.0D).color((c0 >> 16) & 0xFF, (c0 >> 8) & 0xFF, c0 & 0xFF, 0xFF).endVertex();
			vertexbuffer.vertex(x1, y0, 0.0D).color((c1 >> 16) & 0xFF, (c1 >> 8) & 0xFF, c1 & 0xFF, 0xFF).endVertex();
			vertexbuffer.vertex(x1, y1, 0.0D).color((c1 >> 16) & 0xFF, (c1 >> 8) & 0xFF, c1 & 0xFF, 0xFF).endVertex();
			vertexbuffer.vertex(x0, y1, 0.0D).color((c0 >> 16) & 0xFF, (c0 >> 8) & 0xFF, c0 & 0xFF, 0xFF).endVertex();

			x0 = x1;
			x1 += xStep;
			c0 = c1;
		}

		final float lSpan = (height - 14) / LUMA_SLICE_COUNT;
		final float cSpan = height * 2 / CHROMA_SLICE_COUNT;

		for (int lum = LUMA_SLICE_COUNT - 1; lum >= 0; --lum) {
			for (int chr = 0; chr <  CHROMA_SLICE_COUNT; ++chr) {

				final int c = mix[lum][chr];

				if (c == NO_COLOR) {
					continue;
				}

				final int r = (c >> 16) & 0xFF;
				final int g = (c >> 8) & 0xFF;
				final int b = c & 0xFF;

				final float cx0 = left + cSpan * chr;
				final float cx1 = cx0 + cSpan;

				final float ly0 = bottom - lSpan * lum;
				final float ly1 = ly0 - lSpan;

				vertexbuffer.vertex(cx0, ly0, 0.0D).color(r, g, b, 0xFF).endVertex();
				vertexbuffer.vertex(cx1, ly0, 0.0D).color(r, g, b, 0xFF).endVertex();
				vertexbuffer.vertex(cx1, ly1, 0.0D).color(r, g, b, 0xFF).endVertex();
				vertexbuffer.vertex(cx0, ly1, 0.0D).color(r, g, b, 0xFF).endVertex();

			}
		}

		// highlight current color
		final int hc = effectiveChroma / CHROMA_SLICE_SIZE;
		final int hl = luminance / LUMA_SLICE_SIZE;
		final int hrgb = mix[hl][hc];

		if (hrgb != NO_COLOR) {
			final int r = (hrgb >> 16) & 0xFF;
			final int g = (hrgb >> 8) & 0xFF;
			final int b = hrgb & 0xFF;

			final float cx = left + cSpan * hc + cSpan * 0.5f;
			final float ly = bottom - lSpan * hl - cSpan * 0.5f;

			float cx0 = cx - 4.0f;
			float cx1 = cx + 4.0f;

			float ly0 = ly - 4.0f;
			float ly1 = ly + 4.0f;

			final int c = luminance > 50 ? 0 : 0xFF;

			vertexbuffer.vertex(cx0, ly1, 0.0D).color(c, c, c, 0xFF).endVertex();
			vertexbuffer.vertex(cx1, ly1, 0.0D).color(c, c, c, 0xFF).endVertex();
			vertexbuffer.vertex(cx1, ly0, 0.0D).color(c, c, c, 0xFF).endVertex();
			vertexbuffer.vertex(cx0, ly0, 0.0D).color(c, c, c, 0xFF).endVertex();

			cx0 += 0.5f;
			cx1 -= 0.5f;
			ly0 += 0.5f;
			ly1 -= 0.5f;

			vertexbuffer.vertex(cx0, ly1, 0.0D).color(r, g, b, 0xFF).endVertex();
			vertexbuffer.vertex(cx1, ly1, 0.0D).color(r, g, b, 0xFF).endVertex();
			vertexbuffer.vertex(cx1, ly0, 0.0D).color(r, g, b, 0xFF).endVertex();
			vertexbuffer.vertex(cx0, ly0, 0.0D).color(r, g, b, 0xFF).endVertex();
		}

		BufferUploader.drawWithShader(vertexbuffer.end());
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
	}

	private boolean changeHueIfDifferent(int newHue) {
		if (newHue != hue) {
			hue = newHue;
			rebuildMix();

			int c = chroma;

			int rgb = HCL_TO_RGB.getOrDefault(hue | (c << 16) | (luminance << 24), NO_COLOR);

			// reduce effective chroma until we have a visible color
			while (c > 0 && rgb  == NO_COLOR) {
				c -= CHROMA_SLICE_SIZE;
				rgb = HCL_TO_RGB.getOrDefault(hue | (c << 16) | (luminance << 24), NO_COLOR);

			}

			effectiveChroma = c;
			this.rgb = rgb;

			return true;
		} else {
			return false;
		}
	}

	private void rebuildMix() {
		for (int lum = 0; lum < LUMA_SLICE_COUNT; ++lum) {
			final int hl = hue | ((lum * LUMA_SLICE_SIZE) << 24);

			for (int chr = 0; chr < CHROMA_SLICE_COUNT; ++chr) {
				mix[lum][chr] = HCL_TO_RGB.get(hl | ((chr * CHROMA_SLICE_SIZE) << 16));
			}
		}
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		if (mouseY >= top && mouseY <= top + 11) {
			final int angle = (int) Math.round(360F * (mouseX - left) / width);

			if (angle >= 0 && angle <= 360) {
				if (changeHueIfDifferent(angle)) {
					onRgbChange.accept(rgb);
				}
			}
		} else {
			final float lSpan = (height - 14) / LUMA_SLICE_COUNT;
			final float cSpan = height * 2 / CHROMA_SLICE_COUNT;

			final int c = (int) Math.round((mouseX - left) / cSpan);
			final int l = (int) Math.round((mouseY - bottom) / -lSpan);

			if (c < CHROMA_SLICE_COUNT && l < LUMA_SLICE_COUNT && c >= 0 && l >= 0) {
				final int hcl = hue  | ((c * CHROMA_SLICE_SIZE) << 16) | ((l * LUMA_SLICE_SIZE) << 24);

				final int rgb = HCL_TO_RGB.getOrDefault(hcl, NO_COLOR);

				if (rgb != NO_COLOR) {
					this.rgb = rgb;
					onRgbChange.accept(rgb);
					luminance = (hcl >> 24) & 0xFF;
					chroma = (hcl >> 16) & 0xFF;
					effectiveChroma = chroma;
				}
			}
		}
	}

	@Override
	protected void handleMouseDrag(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		handleMouseClick(mouseX, mouseY, clickedMouseButton);
	}

	@Override
	protected void handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {
		final int inc = mouseIncrementDelta();

		if (inc != 0) {
			int newHue = hue + inc;
			if (newHue < 0) {
				newHue += 360;
			} else if (newHue >= 360) {
				newHue -= 360;
			}

			if (changeHueIfDifferent(newHue)) {
				onRgbChange.accept(rgb);
			}
		}
	}

	@Override
	public void drawToolTip(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// NOOP

	}
}
