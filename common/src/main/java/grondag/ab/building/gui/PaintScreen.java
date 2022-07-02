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

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import grondag.ab.ux.client.AbstractSimpleScreen;
import grondag.ab.ux.client.GuiUtil;
import grondag.ab.ux.client.ScreenTheme;
import grondag.ab.ux.client.color.BlockColors;
import grondag.ab.ux.client.control.Button;
import grondag.ab.ux.client.control.Slider;
import grondag.ab.ux.client.control.Toggle;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.paint.XmPaintFinder;
import grondag.xm.api.texture.TextureSet;
import grondag.xm.api.texture.XmTextures;
import grondag.xm.modelstate.AbstractPrimitiveModelState;

public class PaintScreen extends AbstractSimpleScreen {
	private static final int PREVIEW_SIZE = 80;
	private static final int TEXTURE_SIZE = 40;
	private static final int MARGIN = 4;
	private static final int SPACING = 8;
	private static final int BUTTON_WIDTH = 60;
	private static final int BUTTON_HEIGHT = 20;

	protected ColorPicker colorPicker;
	protected ModelPreview modelPreview;
	protected TexturePicker texturePicker;
	protected Toggle aoToggle;
	protected Toggle diffuseToggle;
	protected Toggle emissiveToggle;
	protected Slider alphaSlider;

	protected final ItemStack stack;
	protected final InteractionHand hand;

	private int selectedLayer =  0;

	protected LayerSelector[] layers = new LayerSelector[3];

	protected final XmPaintFinder finder = XmPaint.finder();

	public PaintScreen(ItemStack stack, InteractionHand hand) {
		this.stack = stack;
		this.hand = hand;
	}

	@Override
	protected void computeScreenBounds() {
		screenWidth = MARGIN + PREVIEW_SIZE + SPACING + (TEXTURE_SIZE + MARGIN) * 5 + MARGIN + ScreenTheme.current().tabMargin + ScreenTheme.current().tabWidth + MARGIN;
		screenLeft = (width - screenWidth) / 2;
		screenHeight = MARGIN + PREVIEW_SIZE + SPACING + (TEXTURE_SIZE + MARGIN) * 3 + MARGIN + BUTTON_HEIGHT + MARGIN;
		screenTop = (height - screenHeight) / 2;
	}

	@Override
	public void renderBackground(PoseStack matrices) {
		super.renderBackground(matrices);
		GuiUtil.drawRect(matrices.last().pose(), screenLeft, screenTop, screenLeft + screenWidth, screenTop + screenHeight, 0xFF202020);
	}

	@Override
	public void addControls() {
		modelPreview = new ModelPreview(this);
		modelPreview.setLeft(screenLeft + MARGIN);
		modelPreview.setTop(screenTop + MARGIN);
		modelPreview.setWidth(PREVIEW_SIZE);
		modelPreview.setHeight(PREVIEW_SIZE);
		modelPreview.setStack(stack);
		addRenderableWidget(modelPreview);

		// NB: need to come before color picker in child list to get mouse events
		aoToggle = new Toggle(this);
		// TODO: localize, also below
		aoToggle.setLabel(Component.literal("AO"));
		aoToggle.setLeft(screenLeft + screenWidth - MARGIN - 65);
		aoToggle.setTop(screenTop + MARGIN + BUTTON_HEIGHT);
		aoToggle.setHeight(12);
		aoToggle.setWidth(30);
		aoToggle.onChanged(this::updateAo);
		addRenderableWidget(aoToggle);

		diffuseToggle = new Toggle(this);
		diffuseToggle.setLabel(Component.literal("Diffuse"));
		diffuseToggle.setLeft(screenLeft + screenWidth - MARGIN - 65);
		diffuseToggle.setTop(screenTop + MARGIN + BUTTON_HEIGHT + BUTTON_HEIGHT);
		diffuseToggle.setHeight(12);
		diffuseToggle.setWidth(30);
		diffuseToggle.onChanged(this::updateDiffuse);
		addRenderableWidget(diffuseToggle);

		emissiveToggle = new Toggle(this);
		emissiveToggle.setLabel(Component.literal("Emissive"));
		emissiveToggle.setLeft(screenLeft + screenWidth - MARGIN - 65);
		emissiveToggle.setTop(screenTop + MARGIN + BUTTON_HEIGHT + BUTTON_HEIGHT + BUTTON_HEIGHT);
		emissiveToggle.setHeight(12);
		emissiveToggle.setWidth(30);
		emissiveToggle.onChanged(this::updateEmissive);
		addRenderableWidget(emissiveToggle);

		colorPicker = new ColorPicker(this);
		colorPicker.setLeft(screenLeft + MARGIN + PREVIEW_SIZE + SPACING);
		colorPicker.setTop(screenTop + MARGIN);
		colorPicker.setWidth(screenWidth - MARGIN - PREVIEW_SIZE - SPACING - MARGIN);
		colorPicker.setHeight(PREVIEW_SIZE);
		colorPicker.onChange(this::updateColor);
		addRenderableWidget(colorPicker);

		texturePicker = new TexturePicker(this);
		texturePicker.setLeft(screenLeft + MARGIN + PREVIEW_SIZE + SPACING);
		texturePicker.setTop(screenTop + MARGIN + PREVIEW_SIZE + SPACING);
		texturePicker.setWidth(screenWidth - MARGIN - PREVIEW_SIZE - SPACING - MARGIN);
		texturePicker.setHeight(screenHeight - MARGIN - PREVIEW_SIZE - SPACING - MARGIN - theme.singleLineWidgetHeight - MARGIN);
		texturePicker.setItemSize(TEXTURE_SIZE);
		texturePicker.setItemSpacing(MARGIN);
		texturePicker.setItemsPerRow();
		texturePicker.onChanged(this::updateTexture);
		addRenderableWidget(texturePicker);

		final LayerSelector layer0 = new LayerSelector(this);
		layer0.setLeft(screenLeft + MARGIN);
		layer0.setTop(screenTop + MARGIN + PREVIEW_SIZE + SPACING);
		layer0.setWidth(PREVIEW_SIZE);
		layer0.setHeight(TEXTURE_SIZE + MARGIN);
		layer0.setItemSize(TEXTURE_SIZE);
		layer0.setItemSpacing(MARGIN);
		layer0.onAction(a -> updateLayer(0, a));
		layer0.setClearable(false);
		layer0.setSelected(true);
		layers[0] = layer0;
		addRenderableWidget(layer0);

		final LayerSelector layer1 = new LayerSelector(this);
		layer1.setLeft(screenLeft + MARGIN);
		layer1.setTop(screenTop + MARGIN + PREVIEW_SIZE + SPACING + TEXTURE_SIZE + MARGIN);
		layer1.setWidth(PREVIEW_SIZE);
		layer1.setHeight(TEXTURE_SIZE + MARGIN);
		layer1.setItemSize(TEXTURE_SIZE);
		layer1.setItemSpacing(MARGIN);
		layer1.onAction(a -> updateLayer(1, a));
		layers[1] = layer1;
		addRenderableWidget(layer1);


		final LayerSelector layer2 = new LayerSelector(this);
		layer2.setLeft(screenLeft + MARGIN);
		layer2.setTop(screenTop + MARGIN + PREVIEW_SIZE + SPACING + TEXTURE_SIZE + MARGIN + TEXTURE_SIZE + MARGIN);
		layer2.setWidth(PREVIEW_SIZE);
		layer2.setHeight(TEXTURE_SIZE + MARGIN);
		layer2.setItemSize(TEXTURE_SIZE);
		layer2.setItemSpacing(MARGIN);
		layer2.onAction(a -> updateLayer(2, a));
		layers[2] = layer2;
		addRenderableWidget(layer2);

		final int buttonX = screenLeft + screenWidth - MARGIN - BUTTON_WIDTH;
		final int buttonY = screenTop + screenHeight - MARGIN - BUTTON_HEIGHT;

		final Button done = new Button(this, buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, Component.translatable("gui.done")) {
			@Override
			public void onPress() {
				UpdateStackPaintC2S.send(modelPreview.modelState(), hand);
				PaintScreen.this.onClose();
			}
		};

		this.addRenderableWidget(done);

		alphaSlider = new Slider(this, 256, Component.literal("Alpha"), 0.15f);
		alphaSlider.setLeft(screenLeft + MARGIN);
		alphaSlider.setTop(buttonY + MARGIN);
		alphaSlider.setHeight(12);
		alphaSlider.setWidth(256);
		alphaSlider.onChanged(this::updateAlpha);
		addRenderableWidget(alphaSlider);

		readMaterial();
	}

	private void updateColor(int rgb) {
		final XmPaint paint = modelPreview.modelState().paint(0);
		finder.clear();
		finder.copy(paint);
		finder.textureColor(selectedLayer, (paint.textureColor(selectedLayer) & 0xFF000000) | (rgb & 0xFFFFFF));
		modelPreview.modelState().paint(0, finder.find());
		modelPreview.setModelDirty();
		readMaterial();
	}

	private void updateTexture(TextureSet tex) {
		final AbstractPrimitiveModelState<?, ?, ?> modelState = modelPreview.modelState();
		finder.clear();
		finder.copy(modelState.paint(0));
		finder.texture(selectedLayer, tex);
		modelState.paint(0, finder.find());
		modelPreview.setModelDirty();
		readMaterial();
	}

	private void updateLayer(int index, LayerSelector.Action action) {
		switch(action) {
		case CLEAR: {
			final AbstractPrimitiveModelState<?, ?, ?> modelState = modelPreview.modelState();

			if (index > 0 && index == modelState.paint(0).textureDepth() - 1) {
				finder.clear();
				finder.copy(modelState.paint(0));
				finder.textureDepth(index);
				modelState.paint(0, finder.find());
				modelPreview.setModelDirty();
			}

			if (index == selectedLayer) {
				layers[index].setSelected(false);
				--selectedLayer;
				layers[selectedLayer].setSelected(true);
			}

			readMaterial();
			break;
		}

		case CREATE: {
			final AbstractPrimitiveModelState<?, ?, ?> modelState = modelPreview.modelState();

			layers[selectedLayer].setSelected(false);
			selectedLayer = index;
			layers[index].setSelected(true);
			finder.clear();
			finder.copy(modelState.paint(0));
			finder.textureDepth(index + 1);
			finder.texture(index, XmTextures.TILE_NOISE_SUBTLE);
			finder.textureColor(index, BlockColors.DEFAULT_WHITE_RGB);
			modelState.paint(0, finder.find());
			modelPreview.setModelDirty();
			readMaterial();
			break;
		}

		default:
		case SELECT:
			layers[selectedLayer].setSelected(false);
			selectedLayer = index;
			layers[index].setSelected(true);
			readMaterial();
			break;
		}
	}

	private void readMaterial() {
		final XmPaint paint = modelPreview.modelState().paint(0);
		final int depth = paint.textureDepth();

		for (int i = 0; i < depth; ++i) {
			layers[i].setTexture(paint.texture(i));
			layers[i].setRgb(paint.textureColor(i));
		}

		layers[1].setClearable(depth == 2);
		layers[2].setVisible(depth > 1);
		layers[2].setClearable(depth == 3);

		if (selectedLayer < depth) {
			colorPicker.setRgb(paint.textureColor(selectedLayer) | 0xFF000000);
			texturePicker.setRgb(paint.textureColor(selectedLayer));
			aoToggle.setOn(!paint.disableAo(selectedLayer));
			diffuseToggle.setOn(!paint.disableDiffuse(selectedLayer));
			emissiveToggle.setOn(paint.emissive(selectedLayer));
			alphaSlider.setSelectedIndex(paint.textureColor(selectedLayer) >>> 24);
		} else {
			texturePicker.setRgb(-1);
			colorPicker.setRgb(-1);
			aoToggle.setOn(true);
			diffuseToggle.setOn(true);
			emissiveToggle.setOn(false);
			alphaSlider.setSelectedIndex(255);
			assert false : "Invalid state in PaintScreen - selected layer index greater than depth";
		}
	}

	private void updateAo(boolean hasAo) {
		final AbstractPrimitiveModelState<?, ?, ?> modelState = modelPreview.modelState();

		if (selectedLayer < modelState.paint(0).textureDepth()) {
			finder.clear();
			finder.copy(modelState.paint(0));
			finder.disableAo(selectedLayer, !hasAo);
			modelState.paint(0, finder.find());
			modelPreview.setModelDirty();
			readMaterial();
		}
	}

	private void updateDiffuse(boolean hasDiffuse) {
		final AbstractPrimitiveModelState<?, ?, ?> modelState = modelPreview.modelState();

		if (selectedLayer < modelState.paint(0).textureDepth()) {
			finder.clear();
			finder.copy(modelState.paint(0));
			finder.disableDiffuse(selectedLayer, !hasDiffuse);
			modelState.paint(0, finder.find());
			modelPreview.setModelDirty();
			readMaterial();
		}
	}

	private void updateEmissive(boolean isEmissive) {
		final AbstractPrimitiveModelState<?, ?, ?> modelState = modelPreview.modelState();

		if (selectedLayer < modelState.paint(0).textureDepth()) {
			finder.clear();
			finder.copy(modelState.paint(0));
			finder.emissive(selectedLayer, isEmissive);
			modelState.paint(0, finder.find());
			modelPreview.setModelDirty();
			readMaterial();
		}
	}

	private void updateAlpha(int alpha) {
		final AbstractPrimitiveModelState<?, ?, ?> modelState = modelPreview.modelState();

		if (selectedLayer < modelState.paint(0).textureDepth()) {
			finder.clear();
			finder.copy(modelState.paint(0));
			final int oldColor = modelState.paint(0).textureColor(selectedLayer);
			finder.textureColor(selectedLayer, ((alpha & 0xFF) << 24) | (oldColor & 0xFFFFFF));
			modelState.paint(0, finder.find());
			modelPreview.setModelDirty();
			readMaterial();
		}
	}
}
