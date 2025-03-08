package ru.sema1ary.vanish;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import ormlite.ConnectionSourceUtil;
import ormlite.DaoFinder;
import ru.sema1ary.joiner.Joiner;
import ru.sema1ary.vanish.command.VanishCommand;
import ru.sema1ary.vanish.listener.JoinListener;
import ru.sema1ary.vanish.listener.PreJoinListener;
import ru.sema1ary.vanish.model.VanishUser;
import ru.sema1ary.vanish.service.VanishUserService;
import ru.sema1ary.vanish.service.impl.VanishUserServiceImpl;

import ru.vidoskim.bukkit.service.ConfigService;
import ru.vidoskim.bukkit.service.impl.ConfigServiceImpl;
import ru.vidoskim.bukkit.util.LiteCommandUtil;
import service.ServiceManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Vanish extends JavaPlugin implements DaoFinder {
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

        new LiteCommandUtil().create("vanish",
                new VanishCommand(miniMessage, ServiceManager.getService(ConfigService.class),
                        ServiceManager.getService(VanishUserService.class))
        );
    }

    @Override
    public void onDisable() {
        ConnectionSourceUtil.closeConnection(true, connectionSource);
    }

    @SneakyThrows
    private void initConnectionSource() {
        if(ServiceManager.getService(ConfigService.class).get("sql-use")) {
            connectionSource = ConnectionSourceUtil.connectSQLDatabaseWithoutSSL(
                    ServiceManager.getService(ConfigService.class).get("sql-driver"),
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

        connectionSource = ConnectionSourceUtil.connectNoSQLDatabase("sqlite",
                databaseFilePath.toString(), VanishUser.class);
    }
}
