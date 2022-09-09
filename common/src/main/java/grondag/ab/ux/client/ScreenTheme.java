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

package grondag.ab.ux.client;

public class ScreenTheme {
	public int screenBackground = 0xFFCCCCCC;
	public int buttonColorActive = 0xFFFFFFFF;
	public int buttonColorInactive = 0xFFA0A0A0;
	public int buttonColorFocus = 0xFFBAF6FF;
	public int textColorActive = 0xFF000000;
	public int textColorInactive = 0xFFEEEEEE;
	public int textColorFocus = 0xFF000000;
	public int textColorLabel = 0xFFFFFFFF;
	public int textBorder = 0xFF404040;
	public int textBackground = 0xFFA0A0A0;
	public int controlBackground = 0x4AFFFFFF;

	public int internalMargin = 5;
	public int externalMargin = 5;
	public int scrollbarWidth = 10;

	public int itemSlotGradientTop = 0xFFA9A9A9;
	public int itemSlotGradientBottom = 0xFF898989;
	public int itemSize = 16;
	public int itemSpacing = 2;
	public int itemSelectionMargin = 2;
	public int itemCaptionHeight = 8;
	public int itemCaptionColor = 0xFF000000;
	public int itemSlotSpacing = itemSize + itemSpacing;
	public int itemRowHeightWithCaption = itemSize + itemCaptionHeight + itemSpacing;

	public int capacityBarWidth = 4;
	public int capacityFillColor = 0xFF6080FF;
	public int capacityEmptyColor = 0xFF404040;

	public int tabWidth = 8;
	public int tabMargin = 2;
	public int singleLineWidgetHeight = 10;
}
