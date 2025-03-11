package ru.sema1ary.vanish;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import ru.sema1ary.vanish.command.VanishCommand;
import ru.sema1ary.vanish.listener.JoinListener;
import ru.sema1ary.vanish.listener.PreJoinListener;
import ru.sema1ary.vanish.model.VanishUser;
import ru.sema1ary.vanish.service.VanishUserService;
import ru.sema1ary.vanish.service.impl.VanishUserServiceImpl;

import ru.sema1ary.vedrocraftapi.command.LiteCommandBuilder;
import ru.sema1ary.vedrocraftapi.ormlite.ConnectionSourceUtil;
import ru.sema1ary.vedrocraftapi.ormlite.DaoFinder;
import ru.sema1ary.vedrocraftapi.service.ConfigService;
import ru.sema1ary.vedrocraftapi.service.ServiceGetter;
import ru.sema1ary.vedrocraftapi.service.ServiceManager;
import ru.sema1ary.vedrocraftapi.service.impl.ConfigServiceImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Vanish extends JavaPlugin implements DaoFinder, ServiceGetter {
    private JdbcPooledConnectionSource connectionSource;
    private boolean isJoinerEnabled = false;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ServiceManager.registerService(ConfigService.class, new ConfigServiceImpl(this));

        initConnectionSource();

        if(getServer().getPluginManager().isPluginEnabled("joiner")) {
            isJoinerEnabled = true;
        }

        ServiceManager.registerService(VanishUserService.class, new VanishUserServiceImpl(this, isJoinerEnabled, getDao(connectionSource,
                VanishUser.class)));

        getServer().getPluginManager().registerEvents(new PreJoinListener(ServiceManager.getService(
                VanishUserService.class)), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this, miniMessage,
                ServiceManager.getService(ConfigService.class),
                ServiceManager.getService(VanishUserService.class)), this);

        LiteCommandBuilder.builder()
                .commands(new VanishCommand(miniMessage, ServiceManager.getService(ConfigService.class),
                        ServiceManager.getService(VanishUserService.class)))
                .build();
    }

    @Override
    public void onDisable() {
        ConnectionSourceUtil.closeConnection(true, connectionSource);
    }

    @SneakyThrows
    private void initConnectionSource() {
        if(ServiceManager.getService(ConfigService.class).get("sql-use")) {
            connectionSource = ConnectionSourceUtil.connectSQL(
                    ServiceManager.getService(ConfigService.class).get("sql-host"),
                    ServiceManager.getService(ConfigService.class).get("sql-database"),
                    ServiceManager.getService(ConfigService.class).get("sql-user"),
                    ServiceManager.getService(ConfigService.class).get("sql-password"),
                    VanishUser.class);
            return;
        }

        Path databaseFilePath = Paths.get("plugins/vanish/database.sqlite");
        if(!Files.exists(databaseFilePath) && !databaseFilePath.toFile().createNewFile()) {
            return;
        }

        connectionSource = ConnectionSourceUtil.connectNoSQLDatabase(
                databaseFilePath.toString(), VanishUser.class);
    }
}
