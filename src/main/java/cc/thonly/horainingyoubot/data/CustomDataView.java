package cc.thonly.horainingyoubot.data;

import cc.thonly.horainingyoubot.data.db.CustomData;

public interface CustomDataView<S, R> {

    R create(S source, CustomData data);

}