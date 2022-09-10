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

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.ab.ux.client.GuiUtil;
import grondag.ab.ux.client.ScreenTheme;
import grondag.ab.ux.client.control.AbstractControl;
import grondag.xm.api.texture.TextureSet;
import grondag.xm.api.texture.tech.TechTextures;

@Environment(EnvType.CLIENT)
public class LayerSelector extends AbstractControl<LayerSelector> {
	protected boolean isSelected = false;
	protected boolean isClearable = true;
	protected int rgb = -1;

	Consumer<Action> onAction = a -> {};

	protected TextureSet tex;

	public LayerSelector(ScreenTheme theme) {
		super(theme);
	}

	protected enum MouseLocation {
		NONE, TEXTURE, CLEAR
	}

	protected MouseLocation currentMouseLocation;
	protected int currentMouseIndex;

	public TextureSet getTexture() {
		return tex;
	}

	public void setTexture(TextureSet tex) {
		this.tex = tex;
	}

	@Override
	protected void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		updateMouseLocation(mouseX, mouseY);
		//RenderSystem.disableLighting();
		RenderSystem.disableDepthTest();

		if (isSelected || currentMouseLocation == MouseLocation.TEXTURE) {
			GuiUtil.drawBoxRightBottom(matrixStack.last().pose(), left, top, left + theme.itemSelectionMargin + theme.itemSize + theme.itemSelectionMargin,
					bottom, 1, currentMouseLocation == MouseLocation.TEXTURE ? theme.buttonColorFocus : theme.buttonColorActive);
		}

		final BufferBuilder buffer =  TextureUtil.setupRendering();

		if (tex == null) {
			TextureUtil.bufferTexture(buffer, left + theme.itemSelectionMargin + 4, top + theme.itemSelectionMargin + 4, theme.itemSize - 8, 0xFF50FF50, TechTextures.DECAL_PLUS);
		} else {
			TextureUtil.bufferTexture(buffer, left + theme.itemSelectionMargin, top + theme.itemSelectionMargin, theme.itemSize, rgb, tex);

			if (isClearable) {
				TextureUtil.bufferTexture(buffer, left + theme.itemSize + theme.itemSelectionMargin * 2 + 4, top + theme.itemSelectionMargin + 4, theme.itemSize - 8, 0xFFFF5050, TechTextures.DECAL_MINUS);
			}
		}

		TextureUtil.tearDownRendering();
	}

	@Override
	public final void drawToolTip(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		updateMouseLocation(mouseX, mouseY);

		if (currentMouseLocation == MouseLocation.TEXTURE && tex != null) {
			GuiUtil.drawLocalizedToolTip(matrixStack, tex.displayNameToken(), mouseX, mouseY);
		}
	}

	private void updateMouseLocation(double mouseX, double mouseY) {
		if (mouseX < left || mouseX > right || mouseY < top || mouseY > bottom) {
			currentMouseLocation = MouseLocation.NONE;
		} else if (mouseX <= left + theme.itemSpacing + theme.itemSize + theme.itemSpacing) {
			currentMouseLocation = MouseLocation.TEXTURE;
		} else {
			currentMouseLocation = MouseLocation.CLEAR;
		}
	}

	@Override
	protected void computeCoordinates() {
		super.computeCoordinates();
		height = top + theme.itemSelectionMargin * 2 + theme.itemSize;
	}

	@Override
	protected void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		updateMouseLocation(mouseX, mouseY);

		switch (currentMouseLocation) {
		case TEXTURE:
			onAction.accept(tex == null ? Action.CREATE : Action.SELECT);
			break;

		case CLEAR:
			tex = null;
			onAction.accept(Action.CLEAR);
			break;

		case NONE:
		default:
			break;

		}
	}

	public void onAction(Consumer<Action> onAction) {
		this.onAction = onAction;
	}

	public void setRgb(int rgb) {
		this.rgb = rgb;
	}

	public void setItemSize(int itemSize) {
		theme.itemSize = itemSize;
	}

	public void setItemSpacing(int itemSpacing) {
		theme.itemSpacing = itemSpacing;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setClearable(boolean isClearable) {
		this.isClearable = isClearable;
	}

	public enum Action  {
		CREATE,
		SELECT,
		CLEAR
	}
}
