/*******************************************************************************
 * Copyright 2019 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package grondag.ab.building.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.ab.ux.client.GuiUtil;
import grondag.ab.ux.client.ScreenRenderContext;
import grondag.ab.ux.client.control.AbstractControl;
import grondag.xm.api.item.XmItem;
import grondag.xm.modelstate.AbstractPrimitiveModelState;

@Environment(EnvType.CLIENT)
public class ModelPreview extends AbstractControl<ModelPreview> {
	public ModelPreview(ScreenRenderContext renderContext) {
		super(renderContext);
	}

	private ItemStack previewItem;
	private AbstractPrimitiveModelState<?, ?, ?> modelState;

	private float contentLeft;
	private float contentTop;
	private float contentSize;

	@SuppressWarnings("resource")
	public void setStack(ItemStack stack) {
		previewItem = stack;

		if (modelState != null) {
			modelState.release();
		}

		modelState = XmItem.modelState(Minecraft.getInstance().level, stack);
	}

	public AbstractPrimitiveModelState<?, ?, ?> modelState() {
		return modelState;
	}

	public void setModelDirty() {
		if (modelState != null) {
			modelState.clearRendering();
		}
	}

	@Override
	public void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (previewItem != null) {
			GuiUtil.renderItemAndEffectIntoGui(renderContext, previewItem, modelState.itemProxy(), contentLeft, contentTop, contentSize);
		}
	}

	@Override
	protected void handleCoordinateUpdate() {
		contentSize = Math.min(width, height);
		contentLeft = left + (width - contentSize) / 2;
		contentTop = top + (height - contentSize) / 2;
	}

	@Override
	public void drawToolTip(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub

	}
}
