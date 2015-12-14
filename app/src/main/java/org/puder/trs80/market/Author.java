package org.puder.trs80.market;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

@Table(databaseName = MarketDatabase.NAME)

public class Author extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    String name;

    // needs to be accessible for DELETE
    List<MarketApp> apps;

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE}, variableName = "apps")
    public List<MarketApp> getMyAnts() {
        if(apps == null) {
            apps = new Select()
                    .from(MarketApp.class)
                    .where(Condition.column(MarketApp$Table.AUTHORMODELCONTAINER_AUTHOR_ID).is(id))
                    .queryList();
        }
        return apps;
    }
}