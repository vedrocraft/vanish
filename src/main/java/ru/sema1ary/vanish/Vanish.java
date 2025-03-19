package ru.sema1ary.vanish;

import org.bukkit.plugin.java.JavaPlugin;
import ru.sema1ary.vanish.command.VanishCommand;
import ru.sema1ary.vanish.listener.JoinListener;
import ru.sema1ary.vanish.listener.PreJoinListener;
import ru.sema1ary.vanish.model.VanishUser;
import ru.sema1ary.vanish.service.VanishUserService;
import ru.sema1ary.vanish.service.impl.VanishUserServiceImpl;

import ru.sema1ary.vedrocraftapi.BaseCommons;
import ru.sema1ary.vedrocraftapi.command.LiteCommandBuilder;
import ru.sema1ary.vedrocraftapi.ormlite.ConnectionSourceUtil;
import ru.sema1ary.vedrocraftapi.ormlite.DatabaseUtil;
import ru.sema1ary.vedrocraftapi.service.ConfigService;
import ru.sema1ary.vedrocraftapi.service.ServiceManager;
import ru.sema1ary.vedrocraftapi.service.impl.ConfigServiceImpl;

public final class Vanish extends JavaPlugin implements BaseCommons {
    private boolean isJoinerEnabled = false;

    @Override
    public void onEnable() {
        ServiceManager.registerService(ConfigService.class, new ConfigServiceImpl(this));

        DatabaseUtil.initConnectionSource(
                this,
                getService(ConfigService.class),
                VanishUser.class
        );

        if(getServer().getPluginManager().isPluginEnabled("joiner")) {
            isJoinerEnabled = true;
        }

        ServiceManager.registerService(VanishUserService.class, new VanishUserServiceImpl(
                this,
                isJoinerEnabled,
                getDao(VanishUser.class)
        ));

        getServer().getPluginManager().registerEvents(new PreJoinListener(
                getService(VanishUserService.class)
        ), this);

        getServer().getPluginManager().registerEvents(new JoinListener(
                this,
                getService(ConfigService.class),
                getService(VanishUserService.class)
        ), this);

        LiteCommandBuilder.builder()
                .commands(new VanishCommand(
                        getService(ConfigService.class),
                        getService(VanishUserService.class)
                ))
                .build();
    }

    @Override
    public void onDisable() {
        ConnectionSourceUtil.closeConnection(true);
    }
}
