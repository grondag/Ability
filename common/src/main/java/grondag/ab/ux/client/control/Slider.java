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

import it.unimi.dsi.fastutil.ints.IntConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.ab.ux.client.GuiUtil;
import grondag.ab.ux.client.HorizontalAlignment;
import grondag.ab.ux.client.Layout;
import grondag.ab.ux.client.ScreenTheme;
import grondag.ab.ux.client.VerticalAlignment;

@Environment(EnvType.CLIENT)
public class Slider extends AbstractControl<Slider> {
	public static final int TAB_MARGIN = 2;
	public static final int TAB_WIDTH = 8;
	public static final int ITEM_SPACING = 4;

	protected int size;
	protected Component label;

	/** In range 0-1, how much of pixelWidth to allow for label. */
	protected float labelWidthFactor = 0;

	/** Actual pixelWidth of the label area. */
	protected float labelWidth = 0;

	/** Point to the right of label area. */
	protected float labelRight;

	/** In range 0-1,, how much pixelWidth to allow for drawing selected option. */
	protected float choiceWidthFactor = 0;

	/** Actual pixelWidth of the selected option area. */
	protected float choiceWidth = 0;

	/** Size of each tab box, 0 if one continuous bar. */
	protected float tabSize;

	/** PixelWidth of area between arrows. */
	protected float scrollWidth;

	/**
	 * x point right of choice, left of arrows, tabs. Same as labelRight if no
	 * choice display.
	 */
	protected float choiceRight;

	protected int selectedTabIndex;

	protected enum MouseLocation {
		NONE, CHOICE, LEFT_ARROW, RIGHT_ARROW, TAB
	}

	protected MouseLocation currentMouseLocation;
	protected int currentMouseIndex;
	protected IntConsumer onChanged;

	/**
	 * Size refers to the number of choices in the slider. Minecraft reference is
	 * needed to set height to font height. labelWidth is in range 0-1 and allows
	 * for alignment of stacked controls.
	 */
	@SuppressWarnings("resource")
	public Slider(ScreenTheme theme, int size, Component label, float labelWidthFactor) {
		super(theme);
		this.size = size;
		this.label = label;
		this.labelWidthFactor = labelWidthFactor;
		height(Math.max(TAB_WIDTH, Minecraft.getInstance().font.lineHeight + theme.internalMargin));
		verticalLayout(Layout.FIXED);
	}

	public void setSize(int size) {
		this.size = size;
	}

	protected void drawChoice(PoseStack matrixStack, float partialTicks) {
		// not drawn in base implementation
	}

	@Override
	protected void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (size == 0) {
			return;
		}

		updateMouseLocation(mouseX, mouseY);

		// draw label if there is one
		if (label != null && labelWidth > 0) {
			GuiUtil.drawAlignedStringNoShadow(matrixStack, label, left, top, labelWidth, height, theme.textColorLabel,
					HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
		}

		if (choiceWidthFactor > 0) {
			drawChoice(matrixStack, partialTicks);
		}

		// skip drawing tabs if there is only one
		if (size <= 1) {
			return;
		}

		// if tabs are too small, just do a continuous bar
		float tabStartX = choiceRight + TAB_WIDTH + ITEM_SPACING;
		final float tabTop = top + (height - TAB_WIDTH) / 2;
		final float tabBottom = tabTop + TAB_WIDTH;

		if (tabSize == 0.0) {
			GuiUtil.drawRect(matrixStack.last().pose(), tabStartX, tabTop, tabStartX + scrollWidth, tabBottom, theme.buttonColorInactive);

			// box pixelWidth is same as tab height, so need to have it be half that extra
			// to the right so that we keep our margins with the arrows
			final float selectionCenterX = tabStartX + TAB_WIDTH * 0.5f + (scrollWidth - TAB_WIDTH) * selectedTabIndex / (size - 1);

			GuiUtil.drawRect(matrixStack.last().pose(), selectionCenterX - TAB_WIDTH * 0.5f, tabTop, selectionCenterX + TAB_WIDTH * 0.5f, tabBottom, theme.buttonColorActive);
		} else {
			final int highlightIndex = currentMouseLocation == MouseLocation.TAB ? currentMouseIndex : -1;

			for (int i = 0; i < size; i++) {
				GuiUtil.drawRect(matrixStack.last().pose(), tabStartX, tabTop, tabStartX + tabSize, tabBottom,
						i == highlightIndex ? theme.buttonColorFocus : i == selectedTabIndex ? theme.buttonColorActive : theme.buttonColorInactive);
				tabStartX += (tabSize + TAB_MARGIN);
			}
		}

		final float arrowCenterY = tabTop + TAB_WIDTH * 0.5f;

		GuiUtil.drawQuad(matrixStack.last().pose(), choiceRight, arrowCenterY, choiceRight + TAB_WIDTH, tabBottom, choiceRight + TAB_WIDTH, tabTop, choiceRight,
				arrowCenterY, currentMouseLocation == MouseLocation.LEFT_ARROW ? theme.buttonColorFocus : theme.buttonColorInactive);

		GuiUtil.drawQuad(matrixStack.last().pose(), right, arrowCenterY, right - TAB_WIDTH, tabTop, right - TAB_WIDTH, tabBottom, right, arrowCenterY,
				currentMouseLocation == MouseLocation.RIGHT_ARROW ? theme.buttonColorFocus : theme.buttonColorInactive);
	}

	private void updateMouseLocation(double mouseX, double mouseY) {
		if (size == 0) {
			return;
		}

		if (mouseX < choiceRight || mouseX > right || mouseY < top || mouseY > top + TAB_WIDTH) {
			currentMouseLocation = MouseLocation.NONE;
		} else if (mouseX <= choiceRight + TAB_WIDTH + ITEM_SPACING / 2.0) {
			currentMouseLocation = MouseLocation.LEFT_ARROW;
		} else if (mouseX >= right - TAB_WIDTH - ITEM_SPACING / 2.0) {
			currentMouseLocation = MouseLocation.RIGHT_ARROW;
		} else {
			currentMouseLocation = MouseLocation.TAB;
			currentMouseIndex = Mth.clamp((int) ((mouseX - choiceRight - TAB_WIDTH - ITEM_SPACING / 2) / (scrollWidth) * size), 0,
					size - 1);
		}
	}

	@Override
	protected void computeCoordinates() {
		super.computeCoordinates();

		if (size != 0) {
			labelWidth = width * labelWidthFactor;
			choiceWidth = width * choiceWidthFactor;
			labelRight = left + labelWidth;
			choiceRight = labelRight + choiceWidth + theme.internalMargin;
			scrollWidth = width - labelWidth - choiceWidth - theme.internalMargin - (TAB_WIDTH + ITEM_SPACING) * 2;
			tabSize = size <= 0 ? 0 : (scrollWidth - (TAB_MARGIN * (size - 1))) / size;

			if (tabSize < TAB_MARGIN * 2) {
				tabSize = 0;
			}
		}
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		if (size == 0) {
			return;
		}

		updateMouseLocation(mouseX, mouseY);
		switch (currentMouseLocation) {
			case LEFT_ARROW:
				if (selectedTabIndex > 0) {
					--selectedTabIndex;
					onChanged.accept(selectedTabIndex);
				}

				GuiUtil.playPressedSound();
				break;

			case RIGHT_ARROW:
				if (selectedTabIndex < size - 1) {
					++selectedTabIndex;
					onChanged.accept(selectedTabIndex);
				}

				GuiUtil.playPressedSound();
				break;

			case TAB:
				selectedTabIndex = currentMouseIndex;
				onChanged.accept(selectedTabIndex);
				break;

			case NONE:
			default:
				break;
		}
	}

	@Override
	protected void handleMouseDrag(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		if (size == 0) {
			return;
		}

		updateMouseLocation(mouseX, mouseY);

		if (currentMouseLocation == MouseLocation.TAB) {
			selectedTabIndex = currentMouseIndex;
			onChanged.accept(selectedTabIndex);
		}
	}

	@Override
	protected void handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {
		if (size == 0) {
			return;
		}

		final int oldIndex = selectedTabIndex;
		selectedTabIndex = Mth.clamp(selectedTabIndex + mouseIncrementDelta(), 0, size - 1);

		if (oldIndex != selectedTabIndex) {
			onChanged.accept(selectedTabIndex);
		}
	}

	public int size() {
		return size;
	}

	public void setSelectedIndex(int index) {
		selectedTabIndex = size == 0 ? NO_SELECTION : Mth.clamp(index, 0, size - 1);
	}

	public int getSelectedIndex() {
		return size == 0 ? NO_SELECTION : selectedTabIndex;
	}

	@Override
	public void drawToolTip(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
	}

	public void onChanged(IntConsumer onChanged) {
		this.onChanged = onChanged;
	}
}
