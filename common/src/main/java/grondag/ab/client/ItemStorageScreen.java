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

package grondag.ab.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

import net.fabricmc.loader.api.FabricLoader;

import grondag.ab.AbilityConfig;
import grondag.ab.storage.ux.WitBaseContainerMenu;
import grondag.ab.ux.client.AbstractSimpleContainerScreen;
import grondag.ab.ux.client.GuiUtil;
import grondag.ab.ux.client.control.Button;
import grondag.ab.ux.client.control.ItemStackPicker;
import grondag.ab.ux.client.control.TextField;
import grondag.fluidity.base.synch.DiscreteDisplayDelegate;
import grondag.fluidity.base.synch.DiscreteStorageClientDelegate;
import grondag.fluidity.base.synch.DiscreteStorageServerDelegate;
import grondag.fluidity.base.synch.DisplayDelegate;
import grondag.fluidity.base.synch.ItemStorageActionClientHelper;
import grondag.fluidity.impl.DiscreteDisplayDelegateImpl;

public class ItemStorageScreen extends AbstractSimpleContainerScreen<WitBaseContainerMenu<DiscreteStorageServerDelegate>> {
	private static DiscreteStorageClientDelegate DELEGATE = DiscreteStorageClientDelegate.INSTANCE;

	protected int headerHeight;
	protected int storageHeight;

	protected ItemStackPicker<DiscreteDisplayDelegate> stackPicker;
	protected TextField filterField;
	protected int capacityBarLeft;
	protected int itemPickerTop;
	protected int inventoryLeft;

	public ItemStorageScreen(WitBaseContainerMenu<DiscreteStorageServerDelegate> container, Inventory inventory, Component title) {
		// TODO: something something localization
		super(container, inventory, Component.translatable("Ability Storage"));
	}

	@Override
	public void init() {
		DELEGATE.setFilter("");
		// FIXME: put back when TTF font rendering works again - may be a Vanilla issue?
		//textRenderer = WitConfig.useVanillaFonts ? client.textRenderer : FontHackClient.getTextRenderer(FontHackClient.READING_FONT);
		font = minecraft.font;
		preInitLayout();
		super.init();
		computeScreenBounds();
		addControls();
	}

	protected void preInitLayout() {
		imageWidth = theme.externalMargin + theme.capacityBarWidth + theme.internalMargin + ItemStackPicker.idealWidth(theme, 9) + theme.externalMargin;

		headerHeight = theme.singleLineWidgetHeight + theme.externalMargin + theme.internalMargin;
		final int fixedHeight = headerHeight + theme.itemSlotSpacing * 4 + theme.itemSpacing + theme.externalMargin;
		final int availableHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight() - 30 - fixedHeight;
		final int storageRows = Math.min(8, availableHeight / theme.itemRowHeightWithCaption);

		storageHeight = storageRows * theme.itemRowHeightWithCaption;
		imageHeight = fixedHeight + storageHeight;

		/** distance from edge of dialog to start of player inventory area */
		inventoryLeft = theme.externalMargin + theme.capacityBarWidth + theme.internalMargin;

		/** distance from top of dialog to start of player inventory area */
		final int playerInventoryTop = imageHeight - theme.externalMargin - theme.itemSlotSpacing * 4 - theme.itemSpacing;

		int i = 0;

		for (int p = 0; p < 3; ++p) {
			for (int o = 0; o < 9; ++o) {
				final Slot oldSlot = menu.getSlot(i);
				final Slot newSlot = new Slot(oldSlot.container, o + p * 9 + 9, inventoryLeft + o * theme.itemSlotSpacing, playerInventoryTop + p * theme.itemSlotSpacing);
				newSlot.index = oldSlot.index;
				menu.slots.set(i++, newSlot);
			}
		}

		final int rowTop = playerInventoryTop + theme.itemSlotSpacing * 3 + theme.itemSpacing;

		for (int p = 0; p < 9; ++p) {
			final Slot oldSlot = menu.getSlot(i);
			final Slot newSlot = new Slot(oldSlot.container, p, inventoryLeft + p * theme.itemSlotSpacing, rowTop);
			newSlot.index = oldSlot.index;
			menu.slots.set(i++, newSlot);
		}
	}

	protected void computeScreenBounds() {
		topPos = (height - imageHeight) / 2;

		// if using REI, center on left 2/3 of screen to allow more room for REI
		if (AbilityConfig.shiftScreensLeftIfReiPresent && FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
			leftPos = ((width * 2 / 3) - imageWidth) / 2;
		} else {
			leftPos = (width - imageWidth) / 2;
		}

		// leave room for REI at bottom if vertical margins are tight
		if (topPos <= 30) {
			topPos = 10;
		}
	}

	protected void addControls() {
		capacityBarLeft = leftPos + theme.externalMargin;
		itemPickerTop = topPos + headerHeight;
		stackPicker = new ItemStackPicker<>(theme, DELEGATE.LIST, ItemStorageActionClientHelper::selectAndSend, d -> d.article().toStack(), DiscreteDisplayDelegate::getCount);
		stackPicker.setItemsPerRow(9);

		stackPicker.left(leftPos + inventoryLeft);
		stackPicker.width(ItemStackPicker.idealWidth(theme, 9));
		stackPicker.top(itemPickerTop);
		stackPicker.height(storageHeight);
		addRenderableWidget(stackPicker);

		final Button butt = new Button(theme,
				leftPos + imageWidth - 40 - theme.externalMargin, topPos + theme.externalMargin,
				40, theme.singleLineWidgetHeight,
				DisplayDelegate.getSortText(DELEGATE.getSortIndex())
		) {
			@Override
			public void onPress() {
				final int oldSort = DELEGATE.getSortIndex();
				final int newSort = (oldSort + 1) % DiscreteDisplayDelegateImpl.SORT_COUNT;
				DELEGATE.setSortIndex(newSort);
				setMessage(DisplayDelegate.getSortText(newSort));
				DELEGATE.refreshListIfNeeded();
			}
		};

		addRenderableWidget(butt);

		filterField = new TextField(theme,
				leftPos + inventoryLeft, topPos + theme.externalMargin,
				80, theme.singleLineWidgetHeight, Component.empty());
		filterField.setMaxLength(32);
		filterField.setSelected(true);
		filterField.setChangedListener(s -> DELEGATE.setFilter(s));

		addRenderableWidget(filterField);

		setInitialFocus(filterField);
	}

	@Override
	protected void drawDirectContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		//PERF: do less frequently
		DELEGATE.refreshListIfNeeded();

		final int barHeight = imageHeight - theme.externalMargin * 2;
		final int fillHeight = DELEGATE.capacity() == 0 ? 0 : (int) (barHeight * DELEGATE.usedCapacity() / DELEGATE.capacity());

		// WIP: make this a proper control
		// capacity bar
		final int barBottom = topPos + theme.externalMargin + barHeight;
		GuiUtil.drawRect(matrixStack.last().pose(), capacityBarLeft, topPos + theme.externalMargin,
				capacityBarLeft + theme.capacityBarWidth, barBottom, theme.capacityEmptyColor);

		GuiUtil.drawRect(matrixStack.last().pose(), capacityBarLeft, barBottom - fillHeight,
				capacityBarLeft + theme.capacityBarWidth, barBottom, theme.capacityFillColor);

		// Draw here because drawforeground currently happens after this
		if (DELEGATE.capacity() > 0 && mouseX >= leftPos + theme.externalMargin && mouseX <= leftPos + theme.externalMargin + theme.capacityBarWidth
				&& mouseY >= topPos + theme.externalMargin && mouseY <= topPos + imageHeight - theme.externalMargin
		) {
			//UGLY: standardize how tooltip coordinates work - wants screen relative but getting window relative as inputs here
			this.renderTooltip(matrixStack, Component.literal(DELEGATE.usedCapacity() + " / " + DELEGATE.capacity()), mouseX - leftPos, mouseY - topPos);
		}
	}

	@Override
	protected void renderLabels(PoseStack matrices, int mouseX, int mouseY) {
		// don't draw text
		// NOOP
	}

	@Override
	public boolean mouseClicked(double x, double y, int mouseButton) {
		return super.mouseClicked(x, y, mouseButton);
	}

	@Override
	public boolean mouseDragged(double onX, double onY, int mouseButton, double fromX, double fromY) {
		final Slot slot = findHoveredSlot(onX, onY);

		if (!minecraft.options.touchscreen().get() && !isQuickCrafting && slot != null && slot.hasItem() && hasShiftDown() && minecraft.player.containerMenu.getCarried().isEmpty()) {
			slotClicked(slot, slot.index, mouseButton, ClickType.QUICK_MOVE);
			return true;
		}

		return super.mouseDragged(onX, onY, mouseButton, fromX, fromY);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return super.mouseReleased(d, e, i);
	}

	@Override
	protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType slotActionType) {
		super.slotClicked(slot, slotId, mouseButton, slotActionType);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return super.keyPressed(i, j, k);
	}
}
