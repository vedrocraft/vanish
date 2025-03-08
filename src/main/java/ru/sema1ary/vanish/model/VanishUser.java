package ru.sema1ary.vanish.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sema1ary.vanish.dao.impl.VanishUserDaoImpl;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "players", daoClass = VanishUserDaoImpl.class)
public class VanishUser {
    @DatabaseField(unique = true, generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false)
    private String username;

    @DatabaseField(canBeNull = false, columnName = "is_vanish_active")
    private boolean isVanishActive;
}
