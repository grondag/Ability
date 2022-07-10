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

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import grondag.ab.ux.client.ScreenTheme;

class PlacementScreenLayout {
	int previewSize;
	int textureSize;
	int margin = 4;
	int spacing = 8;
	int buttonWidth;
	int buttonHeight;
	int screenWidth;
	int screenLeft;
	int screenRight;
	int screenHeight;
	int screenTop;
	int screenBottom;

	int leftMargin;
	int rightMargin;
	int topMargin;
	int bottomMargin;

	void initiatlize(int width, int height, Font font) {
		previewSize = Math.max(40, Math.min(width / 5, height / 4));
		textureSize = previewSize / 2;

		buttonWidth = Math.max(50, Math.max(font.width(Component.translatable("gui.ab.save")), font.width(Component.translatable("gui.ab.cancel"))) + 4);
		buttonHeight = Math.max(20, font.lineHeight + 4);

		final var fixedScreenWidth = previewSize + spacing + margin * 2 + buttonWidth * 2 + ScreenTheme.current().tabMargin + ScreenTheme.current().tabWidth;
		final var availableContentWidth = width - 20 - fixedScreenWidth;
		final var contentColumns = Math.min(8, availableContentWidth / (textureSize + margin));
		screenWidth = fixedScreenWidth + contentColumns * (textureSize + margin);

		final var menuFixedScreenHeight = previewSize + spacing + margin * 8 + buttonHeight;
		final var contentFixedScreenHeight = previewSize + spacing + margin * 4 + buttonHeight + (textureSize + margin) * 3;
		final var idealVisualHeight = Math.min(height * 10 / 8, height - 40);
		screenHeight = Math.max(idealVisualHeight, Math.max(menuFixedScreenHeight, contentFixedScreenHeight));

		screenLeft = (width - screenWidth) / 2;
		screenRight = screenLeft + screenWidth;
		screenTop = (height - screenHeight) / 2;
		screenBottom = screenTop + screenHeight;

		leftMargin = screenLeft + margin;
		rightMargin = screenRight - margin;
		topMargin = screenTop + margin;
		bottomMargin = screenBottom - margin;
	}
}
