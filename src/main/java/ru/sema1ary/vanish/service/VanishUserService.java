package ru.sema1ary.vanish.service;

import lombok.NonNull;
import org.bukkit.entity.Player;
import ru.sema1ary.vanish.model.VanishUser;
import ru.sema1ary.vedrocraftapi.service.UserService;

@SuppressWarnings("unused")
public interface VanishUserService extends UserService<VanishUser> {
    void hidePlayer(@NonNull Player player);

    void showPlayer(@NonNull Player player);
}
