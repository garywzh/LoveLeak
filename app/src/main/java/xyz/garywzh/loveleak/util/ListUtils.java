package xyz.garywzh.loveleak.util;

import java.util.List;

/**
 * Created by garywzh on 2016/10/13.
 */

public class ListUtils {

    public static <T> void mergeListWithoutDuplicates(List<T> toList, List<T> fromList, int maxLength) {
        if (toList.size() != 0) {
            final T lastItemOfToList = toList.get(toList.size() - 1);

            final int fs = fromList.size();
            final int ts = toList.size();
            int length = fs <= ts ? fs : ts;
            length = length <= maxLength ? length : maxLength;

            for (int i = 0; i < length; i++) {
                if (fromList.get(i).equals(lastItemOfToList)) {
                    final int lastIndex = toList.size() - 1;
                    for (int j = 0; j <= i; j++) {
                        toList.remove(lastIndex - j);
                    }
                    break;
                }
            }
        }
        toList.addAll(fromList);
    }
}