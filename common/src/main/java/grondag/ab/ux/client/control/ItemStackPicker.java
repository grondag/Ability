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

package grondag.ab.ux.client.control;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToLongFunction;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.ab.ux.client.GuiUtil;
import grondag.ab.ux.client.ScreenRenderContext;
import grondag.ab.ux.client.ScreenTheme;

@Environment(EnvType.CLIENT)
public class ItemStackPicker<T> extends TabBar<T> {
	protected final MouseHandler<T> itemClickHandler;

	// avoids creating a new instance each frame
	protected final Matrix4f fontMatrix = new Matrix4f();
	protected final Function<T, ItemStack> stackFunc;
	protected final ToLongFunction<T> countFunc;

	// scales the glyphs
	protected float fontDrawScale;

	public ItemStackPicker(ScreenRenderContext renderContext, List<T> items, MouseHandler<T> itemClickHandler, Function<T, ItemStack> stackFunc, ToLongFunction<T> countFunc) {
		super(renderContext, items);
		this.itemClickHandler = itemClickHandler;
		this.stackFunc = stackFunc;
		this.countFunc = countFunc;
		setItemsPerRow(9);
		setSelectionEnabled(false);
	}

	public static int idealWidth(ScreenTheme theme, int itemsPerRow) {
		return theme.itemSlotSpacing * itemsPerRow - theme.itemSpacing + theme.internalMargin + theme.tabWidth;
	}

	// TODO: better labels for higher numbers
	private String getQuantityLabel(long qty) {
		if (qty < 1000) {
			return Long.toString(qty);
		} else if (qty < 10000) {
			return String.format("%.1fK", (float) qty / 1000);
		} else if (qty < 100000) {
			return Long.toString(qty / 1000) + "K";
		} else {
			return "many";
		}
	}

	@Override
	protected void drawItem(PoseStack matrixStack, T item, double left, double top, float partialTicks, boolean isHighlighted) {
		final int x = (int) left;
		final int y = (int) top;

		final ItemStack itemStack = stackFunc.apply(item);
		final var itemRenderer = Minecraft.getInstance().getItemRenderer();

		setBlitOffset(200);
		itemRenderer.blitOffset = 200.0F;

		GuiUtil.renderItemAndEffectIntoGui(itemStack, x, y, itemSize);
		// TODO: support for dragging

		drawQuantity(matrixStack, countFunc.applyAsLong(item), x, y);

		setBlitOffset(0);
		itemRenderer.blitOffset = 0.0F;
	}

	protected void drawQuantity(PoseStack matrixStack, long qty, int left, int top) {
		if (qty < 2) {
			return;
		}

		@SuppressWarnings("resource")
		final Font fontRenderer = Minecraft.getInstance().font;
		final String qtyLabel = getQuantityLabel(qty);

		fontMatrix.setIdentity();
		fontMatrix.multiply(Matrix4f.createScaleMatrix(fontDrawScale, fontDrawScale, 1));
		fontMatrix.multiply(Matrix4f.createTranslateMatrix(0.0f, 0.0f, 200.0f));

		final MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		final float x = (left + 8 - fontRenderer.width(qtyLabel) * 0.5f * fontDrawScale) / fontDrawScale;
		final float y = (top + 17.5f) / fontDrawScale;
		fontRenderer.drawInBatch(qtyLabel, x + 0.15f, y, theme.itemCaptionColor, false, fontMatrix, immediate, true, 0, 15728880);
		fontRenderer.drawInBatch(qtyLabel, x - 0.15f, y, theme.itemCaptionColor, false, fontMatrix, immediate, true, 0, 15728880);
		immediate.endBatch();
	}

	@SuppressWarnings("resource")
	@Override
	protected void computeCoordinates() {
		super.computeCoordinates();
		fontDrawScale = 6f / Minecraft.getInstance().font.lineHeight;
	}

	// FIX: remove or repair
	@Override
	protected void setupItemRendering() {
		//		RenderSystem.disableDepthTest();
		//		RenderSystem.enableRescaleNormal();
		//		RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
		//		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void tearDownItemRendering() {
		//		RenderSystem.disableDepthTest();
		//		RenderSystem.disableRescaleNormal();
		//		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		if (itemClickHandler != null && currentMouseLocation == MouseLocation.ITEM) {
			itemClickHandler.handle(clickedMouseButton, resourceForClickHandler());
		} else {
			super.handleMouseClick(mouseX, mouseY, clickedMouseButton);
		}
	}

	private @Nullable T resourceForClickHandler() {
		return get(currentMouseIndex);
	}

	@Override
	protected void drawItemToolTip(PoseStack matrixStack, T item, int mouseX, int mouseY, float partialTicks) {
		GuiUtil.renderTooltip(matrixStack, stackFunc.apply(item), mouseX, mouseY);
	}

	@Override
	public NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}
}
