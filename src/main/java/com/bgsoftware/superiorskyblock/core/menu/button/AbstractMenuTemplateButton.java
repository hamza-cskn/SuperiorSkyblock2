package com.bgsoftware.superiorskyblock.core.menu.button;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuViewButton;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public abstract class AbstractMenuTemplateButton<V extends MenuView<V, ?>> implements MenuTemplateButton<V> {

    protected static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    private final TemplateItem buttonItem;
    private final GameSound clickSound;
    private final List<String> commands;
    private final String requiredPermission;
    private final GameSound lackPermissionSound;
    private final Class<?> viewButtonType;

    public AbstractMenuTemplateButton(TemplateItem buttonItem, GameSound clickSound, List<String> commands,
                                      String requiredPermission, GameSound lackPermissionSound, Class<?> viewButtonType) {
        this.buttonItem = buttonItem;
        this.clickSound = clickSound;
        this.commands = commands == null ? Collections.emptyList() : Collections.unmodifiableList(commands);
        this.requiredPermission = requiredPermission;
        this.lackPermissionSound = lackPermissionSound;
        this.viewButtonType = viewButtonType;
    }

    public ItemStack getButtonItem() {
        return this.buttonItem == null ? null : this.buttonItem.getBuilder().build();
    }

    public TemplateItem getButtonTemplateItem() {
        return buttonItem;
    }

    public GameSound getClickSound() {
        return this.clickSound;
    }

    public List<String> getClickCommands() {
        return this.commands;
    }

    public String getRequiredPermission() {
        return this.requiredPermission;
    }

    public GameSound getLackPermissionSound() {
        return this.lackPermissionSound;
    }

    @Override
    public Class<?> getViewButtonType() {
        return this.viewButtonType;
    }

    protected <B extends MenuViewButton<V>> B ensureCorrectType(B button) {
        if (!getViewButtonType().isInstance(button))
            throw new IllegalStateException("Menu template " + getClass().getSimpleName() +
                    " expected view button of type " + getViewButtonType().getSimpleName() + ", got " + button.getClass());
        return button;
    }

    public <B extends MenuTemplateButton.Builder<?>> B applyToBuilder(B buttonBuilder) {
        if (((AbstractBuilder<?>) buttonBuilder).buttonItem == null)
            ((AbstractBuilder<?>) buttonBuilder).buttonItem = this.buttonItem;

        buttonBuilder.setClickSound(this.clickSound);
        buttonBuilder.setClickCommands(this.commands);
        buttonBuilder.setRequiredPermission(this.requiredPermission);
        buttonBuilder.setLackPermissionsSound(this.lackPermissionSound);

        return buttonBuilder;
    }

    public static <V extends MenuView<V, ?>> AbstractBuilder<V> newBuilder(Class<?> viewButtonType, MenuViewButtonCreator<V> viewButtonCreator) {
        return new AbstractBuilder<V>() {
            @Override
            public MenuTemplateButton<V> build() {
                return new AbstractMenuTemplateButton<V>(this.buttonItem, this.clickSound, this.commands,
                        this.requiredPermission, this.lackPermissionSound, viewButtonType) {

                    @Override
                    public MenuViewButton<V> createViewButton(V menuView) {
                        return ensureCorrectType(viewButtonCreator.create(this, menuView));
                    }
                };
            }
        };
    }

    public static abstract class AbstractBuilder<V extends MenuView<V, ?>> implements MenuTemplateButton.Builder<V> {

        protected TemplateItem buttonItem = null;
        protected GameSound clickSound = null;
        protected List<String> commands = null;
        protected String requiredPermission = null;
        protected GameSound lackPermissionSound = null;

        public Builder<V> setButtonItem(ItemStack buttonItem) {
            return this.setButtonItem(new TemplateItem(new ItemBuilder(buttonItem)));
        }

        public Builder<V> setButtonItem(TemplateItem buttonItem) {
            this.buttonItem = buttonItem;
            return this;
        }

        public Builder<V> setClickSound(GameSound clickSound) {
            this.clickSound = clickSound;
            return this;
        }

        public Builder<V> setClickCommands(List<String> commands) {
            this.commands = commands;
            return this;
        }

        public Builder<V> setRequiredPermission(String requiredPermission) {
            this.requiredPermission = requiredPermission;
            return this;
        }

        public Builder<V> setLackPermissionsSound(GameSound lackPermissionSound) {
            this.lackPermissionSound = lackPermissionSound;
            return this;
        }

        public abstract MenuTemplateButton<V> build();

    }

}
