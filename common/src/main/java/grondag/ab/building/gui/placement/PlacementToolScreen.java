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

package grondag.ab.building.gui.placement;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import grondag.ab.building.gui.ModelPreview;
import grondag.ab.building.gui.UpdateStackPaintC2S;
import grondag.ab.building.placement.PlacementToolState;
import grondag.ab.ux.client.AbstractSimpleScreen;
import grondag.ab.ux.client.GuiUtil;
import grondag.ab.ux.client.control.Button;
import grondag.xm.api.modelstate.primitive.PrimitiveState;

public class PlacementToolScreen extends AbstractSimpleScreen {
	protected final PlacementScreenLayout layout = new PlacementScreenLayout();
	protected final PlacementToolState toolState = new PlacementToolState();

	protected ModelPreview modelPreview;

	@SuppressWarnings("resource")
	public PlacementToolScreen(ItemStack stack, InteractionHand hand) {
		toolState.load(stack, hand, Minecraft.getInstance().level);
	}

	@Override
	protected void computeScreenBounds() {
		layout.initiatlize(width, height, font);
		screenWidth = layout.screenWidth;
		screenLeft = layout.screenLeft;
		screenHeight = layout.screenHeight;
		screenTop = layout.screenTop;
	}

	@Override
	public void renderBackground(PoseStack matrices) {
		super.renderBackground(matrices);
		GuiUtil.drawRect(matrices.last().pose(), screenLeft, screenTop, screenLeft + screenWidth, screenTop + screenHeight, 0xFF202020);
	}

	protected void addPreview() {
		modelPreview = new ModelPreview(this);
		modelPreview.setLeft(layout.leftMargin);
		modelPreview.setTop(layout.topMargin);
		modelPreview.setWidth(layout.previewSize);
		modelPreview.setHeight(layout.previewSize);
		modelPreview.setStack(toolState.displayStack());
		addRenderableWidget(modelPreview);
	}


	protected void addMainMenuButtons() {
		int menuY = layout.topMargin + layout.previewSize + layout.margin;

		this.addRenderableWidget(new Button(this, layout.leftMargin, menuY, layout.previewSize, layout.buttonHeight, Component.translatable("gui.ab.material")) {
			@Override
			public void onPress() {

			}
		});

		menuY += (layout.buttonHeight + layout.margin);

		this.addRenderableWidget(new Button(this, layout.leftMargin, menuY, layout.previewSize, layout.buttonHeight, Component.translatable("gui.ab.shape")) {
			@Override
			public void onPress() {

			}
		});

		menuY += (layout.buttonHeight + layout.margin);

		this.addRenderableWidget(new Button(this, layout.leftMargin, menuY, layout.previewSize, layout.buttonHeight, Component.translatable("gui.ab.paint")) {
			@Override
			public void onPress() {

			}
		});

		menuY += (layout.buttonHeight + layout.margin);

		this.addRenderableWidget(new Button(this, layout.leftMargin, menuY, layout.previewSize, layout.buttonHeight, Component.translatable("gui.ab.placement")) {
			@Override
			public void onPress() {

			}
		});

		this.addRenderableWidget(new Button(this, layout.leftMargin, layout.bottomMargin - layout.buttonHeight, layout.previewSize, layout.buttonHeight, Component.translatable("gui.ab.load")) {
			@Override
			public void onPress() {

			}
		});
	}

	protected void addPrimaryFooter() {
		this.addRenderableWidget(new Button(
				this, layout.rightMargin - layout.margin - layout.buttonWidth * 2, layout.bottomMargin - layout.buttonHeight,
				layout.buttonWidth, layout.buttonHeight, Component.translatable("gui.ab.cancel")
			) {
				@Override
				public void onPress() {
					PlacementToolScreen.this.onClose();
				}
			});

		this.addRenderableWidget(new Button(
			this, layout.rightMargin - layout.buttonWidth, layout.bottomMargin - layout.buttonHeight,
			layout.buttonWidth, layout.buttonHeight, Component.translatable("gui.ab.save")
		) {
			@Override
			public void onPress() {
				UpdateStackPaintC2S.send(toolState);
				PlacementToolScreen.this.onClose();
			}
		});
	}

	@Override
	public void addControls() {
		addPreview();
		addMainMenuButtons();
		addPrimaryFooter();
		readMaterial();
	}

	protected void updateModelState(PrimitiveState modelState) {
		toolState.modelState(modelState);
		readMaterial();
	}

	protected void readMaterial() {
		assert toolState.modelState() != null : "Invalid state in PaintScreen - missing model state";
		modelPreview.setStack(toolState.displayStack());
	}
}
