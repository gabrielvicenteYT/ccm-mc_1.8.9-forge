package com.wjbaker.ccm.render.gui.screen.screens.drawCrosshair.components;

import com.wjbaker.ccm.CustomCrosshairMod;
import com.wjbaker.ccm.crosshair.custom.CustomCrosshairDrawer;
import com.wjbaker.ccm.crosshair.property.IntegerProperty;
import com.wjbaker.ccm.render.ModTheme;
import com.wjbaker.ccm.render.gui.component.GuiComponent;
import com.wjbaker.ccm.render.gui.screen.GuiScreen;
import com.wjbaker.ccm.type.RGBA;

public final class DrawCrosshairGuiComponent extends GuiComponent {

    private static final int GRID_SIZE = 5;

    private final IntegerProperty imageSize;
    private final CustomCrosshairDrawer customCrosshairDrawer;

    private boolean isDrawing = false;
    private boolean isDragging = false;
    private int lastUpdatedX = -1;
    private int lastUpdatedY = -1;

    public DrawCrosshairGuiComponent(
        final GuiScreen parentGuiScreen,
        final int x,
        final int y,
        final IntegerProperty imageSize) {

        super(parentGuiScreen, x, y, imageSize.get() * GRID_SIZE, imageSize.get() * GRID_SIZE);

        this.imageSize = imageSize;
        this.customCrosshairDrawer = CustomCrosshairMod.INSTANCE.properties().getCustomCrosshairDrawer();
    }

    public void updateSize() {
        this.setSize(this.imageSize.get() * GRID_SIZE, this.imageSize.get() * GRID_SIZE);
    }

    @Override
    public void draw() {
        super.draw();

        int gridCount = this.imageSize.get();

        for (int gridX = 0; gridX < gridCount; ++gridX) {
            for (int gridY = 0; gridY < gridCount; ++gridY) {
                this.renderManager.drawFilledRectangle(
                    this.x + GRID_SIZE * gridX,
                    this.y + GRID_SIZE * gridY,
                    this.x + GRID_SIZE * gridX + GRID_SIZE,
                    this.y + GRID_SIZE * gridY + GRID_SIZE,
                    this.getGridColour(gridX, gridY));

                if (this.customCrosshairDrawer.getAt(gridX, gridY) == 1) {
                    this.renderManager.drawFilledRectangle(
                        this.x + GRID_SIZE * gridX,
                        this.y + GRID_SIZE * gridY,
                        this.x + GRID_SIZE * gridX + GRID_SIZE,
                        this.y + GRID_SIZE * gridY + GRID_SIZE,
                        CustomCrosshairMod.INSTANCE.properties().getCrosshair().colour.get());
                }
            }
        }
    }

    private RGBA getGridColour(final int x, final int y) {
        int imageSize = this.imageSize.get();
        boolean isOdd = imageSize % 2 != 0;
        int middle = imageSize / 2;

        boolean centerWhenOdd = isOdd && x == middle && y == middle;
        boolean centerWhenEven = !isOdd && (x == middle - 1 || x == middle) && (y == middle - 1 || y == middle);

        if (centerWhenOdd || centerWhenEven)
            return ModTheme.DARK_GREY.setOpacity(220);

        if ((x % 2 == 0 && y % 2 == 0) || (x % 2 != 0 && y % 2 != 0))
            return ModTheme.WHITE.setOpacity(140);

        return ModTheme.DARK_GREY.setOpacity(140);
    }

    @Override
    public void onMouseDown(final int mouseX, final int mouseY, final int button) {
        super.onMouseDown(mouseX, mouseY, button);

        if (!this.isInsideComponent(mouseX, mouseY))
            return;

        this.isDrawing = true;
    }

    @Override
    public void onMouseUp(final int mouseX, final int mouseY, final int button) {
        super.onMouseUp(mouseX, mouseY, button);

        this.onDraw(mouseX, mouseY);
        this.isDrawing = false;
        this.isDragging = false;
    }

    @Override
    public void onMouseMove(final int mouseX, final int mouseY) {
        super.onMouseMove(mouseX, mouseY);

        this.isDragging = true;
        this.onDraw(mouseX, mouseY);
    }

    private void onDraw(final int x, final int y) {
        if (!this.isInsideComponent(x, y))
            return;

        if (!this.isDrawing)
            return;

        int relativeX = x - this.getX();
        int relativeY = y - this.getY();

        int imagePositionX = relativeX / GRID_SIZE;
        int imagePositionY = relativeY / GRID_SIZE;

        if (this.isDragging && this.lastUpdatedX == imagePositionX && this.lastUpdatedY == imagePositionY)
            return;

        if (imagePositionX >= 0 && imagePositionX < imageSize.get() &&
            imagePositionY >= 0 && imagePositionY < imageSize.get()) {

            this.customCrosshairDrawer.togglePixel(imagePositionX, imagePositionY);
        }

        this.lastUpdatedX = imagePositionX;
        this.lastUpdatedY = imagePositionY;
    }
}