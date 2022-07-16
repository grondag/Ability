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

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.ab.ux.client.Layout;
import grondag.ab.ux.client.ScreenRenderContext;
import grondag.ab.ux.client.ScreenTheme;

/**
 * Similar to Mojang's Abstract Widget but not based on fixed integer/pixel dimensions.
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractControl<T extends AbstractControl<T>> extends GuiComponent implements GuiEventListener, Widget, NarratableEntry {
	/** Top of this control in screenspace.  Does not reflect any interior margin. */
	protected float top;

	/** Left of this control in screenspace.  Does not reflect any interior margin. */
	protected float left;

	/** Height of this control in screenspace.  Does not reflect any interior margin. */
	protected float height;

	/** Width of this control in screenspace.  Does not reflect any interior margin. */
	protected float width;

	/** Bottom of this control in screenspace.  Does not reflect any interior margin. */
	protected float bottom;

	/** Right of this control in screenspace.  Does not reflect any interior margin. */
	protected float right;

	/** True when coordinate state should be recomputed because inputs may have changed. */
	private boolean coordinatesDirty = false;

	/** True when control should be rendered. */
	protected boolean isVisible = true;

	/** True when control should respond to input. */
	protected boolean isActive = true;

	/** Cumulative scroll distance from all events. */
	protected float scrollDistance;

	/** Cumulative distance before scroll is recognized. */
	protected float scrollIncrementDistance = 1;

	/** Last scroll increment - used to compute a delta. */
	protected int lastScrollIncrement = 0;

	protected int horizontalWeight = 1;
	protected int verticalWeight = 1;

	protected Layout horizontalLayout = Layout.WEIGHTED;
	protected Layout verticalLayout = Layout.WEIGHTED;

	/**
	 * If a control has consistent shape, is height / width. Multiply width by this
	 * number to get height. Divide height by this number to get width.
	 */
	protected float aspectRatio = 1.0f;

	protected final ScreenRenderContext renderContext;

	protected final ScreenTheme theme = ScreenTheme.current();

	public AbstractControl(ScreenRenderContext renderContext) {
		this.renderContext = renderContext;
	}

	public void resize(float left, float top, float width, float height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.setCoordinatesDirty();
	}

	@Override
	public final void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.computeCoordinatesIfNeeded();

		if (this.isVisible) {
			// set hover start, so that controls further down the stack can overwrite
			if (this.isMouseOver(mouseX, mouseY)) {
				renderContext.setHoverControl(this);
			}

			this.drawContent(matrixStack, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public void updateNarration(NarrationElementOutput builder) {
		// TODO whatever this is
	}

	@Override
	public NarrationPriority narrationPriority() {
		// TODO: implement
		return NarrationPriority.NONE;
	}

	public abstract void drawToolTip(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks);

	protected abstract void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks);

	/**
	 * Recomputes all coordinate state and clears coordinate dirty flag.
	 * Use {@link #computeCoordinatesIfNeeded()} if uncertain if update is needed.
	 *
	 * <p>Child classes should always call super method, and call it first.
	 */
	protected void computeCoordinates() {
		this.bottom = this.top + this.height;
		this.right = this.left + this.width;
		coordinatesDirty = false;
	}

	/**
	 * Implements vanilla widget handler, delegating all real work to {@link #handleMouseClick(double, double, int)}.
	 *
	 * <p>A difference from vanilla handler is that we delegate sounds and other behavior to handlers
	 * because we have more varied interactions.
	 */
	@Override
	public final boolean mouseClicked(double mouseX, double mouseY, int clickedMouseButton) {
		if (!isMouseOver(mouseX, mouseY)) return false;
		handleMouseClick(mouseX, mouseY, clickedMouseButton);
		return true;
	}

	/**
	 * Responsible for handling mouse clicks.  Click will be in bounds before called.
	 * Any sounds must be done here.
	 */
	protected void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		// NOOP
	}

	/**
	 * Implements vanilla widget handler, delegating all real work to {@link #handleMouseScroll(double, double, double)}.
	 */
	@Override
	public final boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		if (!isMouseOver(mouseX, mouseY)) return false;
		scrollDistance += scrollDelta;
		handleMouseScroll(mouseX, mouseY, scrollDelta);
		return true;
	}

	protected void handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {
		// NOOP
	}

	/**
	 * Implements vanilla widget handler, delegating all real work to {@link #handleMouseDrag(double, double, int, double, double)}.
	 */
	@Override
	public final boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		if (!isMouseOver(mouseX, mouseY)) return false;
		handleMouseDrag(mouseX, mouseY, clickedMouseButton, dx, dy);
		return true;
	}

	protected void handleMouseDrag(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		// NOOP
	}

	protected int mouseIncrementDelta() {
		final int newIncrement = (int) (this.scrollDistance / this.scrollIncrementDistance);
		final int result = newIncrement - this.lastScrollIncrement;

		if (result != 0) {
			this.lastScrollIncrement = newIncrement;
		}

		return result;
	}

	/**
	 * Checks for dirty coordinates and calls {@link #computeCoordinates()} if found.
	 * Note that {@link #computeCoordinates()} always resets dirty flag and super
	 * implementation must be called.
	 */
	protected final void computeCoordinatesIfNeeded() {
		if (coordinatesDirty) {
			computeCoordinates();
		}
	}

	@SuppressWarnings("unchecked")
	public final T top(float top) {
		this.top = top;
		setCoordinatesDirty();
		return (T) this;
	}

	public final float top() {
		return top;
	}

	@SuppressWarnings("unchecked")
	public final T left(float left) {
		this.left = left;
		setCoordinatesDirty();
		return (T) this;
	}

	public final float left() {
		return left;
	}

	@SuppressWarnings("unchecked")
	public final T width(float width) {
		this.width = width;
		this.setCoordinatesDirty();
		return (T) this;
	}

	public final float width() {
		return width;
	}

	public final float height() {
		return height;
	}

	@SuppressWarnings("unchecked")
	public final T height(float height) {
		this.height = height;
		setCoordinatesDirty();
		return (T) this;
	}

	/**
	 * Sets both width and height same size. Controls that dynamically resize may
	 * result in a non-square actual size. Sometimes life isn't fair.
	 */
	@SuppressWarnings("unchecked")
	public final T squareSize(float size) {
		this.height = size;
		this.width = size;
		setCoordinatesDirty();
		return (T) this;
	}

	public final float right() {
		computeCoordinatesIfNeeded();
		return this.right;
	}

	public final float bottom() {
		computeCoordinatesIfNeeded();
		return this.bottom;
	}

	@SuppressWarnings("unchecked")
	public final T aspectRatio(float aspectRatio) {
		this.aspectRatio = aspectRatio;
		return (T) this;
	}

	public final float aspectRatio() {
		return aspectRatio;
	}

	@SuppressWarnings("unchecked")
	public final T horizontalWeight(int horizontalWeight) {
		this.horizontalWeight = horizontalWeight;
		return (T) this;
	}

	public final int horizontalWeight() {
		return horizontalWeight;
	}

	@SuppressWarnings("unchecked")
	public final T verticalWeight(int verticalWeight) {
		this.verticalWeight = verticalWeight;
		return (T) this;
	}

	public final int verticalWeight() {
		return verticalWeight;
	}

	@SuppressWarnings("unchecked")
	public final T horizontalLayout(Layout horizontalLayout) {
		this.horizontalLayout = horizontalLayout;
		return (T) this;
	}

	public final Layout horizontalLayout() {
		return horizontalLayout;
	}

	@SuppressWarnings("unchecked")
	public final T verticalLayout(Layout verticalLayout) {
		this.verticalLayout = verticalLayout;
		return (T) this;
	}

	public final Layout verticalLayout() {
		return verticalLayout;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return isVisible && isActive & inBounds(mouseX, mouseY);
	}

	/** True when input coordinate within bounds of this control. */
	protected final boolean inBounds(double x, double y) {
		return x >= left && x <= right && y >= top && y <= bottom;
	}

	public final boolean isVisible() {
		return isVisible;
	}

	public final void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	protected final void setCoordinatesDirty() {
		coordinatesDirty = true;
	}

	/** Used in child classes with integer selection indexes to indicate no choice or empty state. */
	public static final int NO_SELECTION = -1;
}
