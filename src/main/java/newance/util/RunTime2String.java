/*
Copyright (C) SIB - Swiss Institute of Bioinformatics, Lausanne, Switzerland
Copyright (C) LICR - Ludwig Institute of Cancer Research, Lausanne, Switzerland
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 2 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
*/

package newance.util;

import java.text.SimpleDateFormat;

/**
 * @author Markus Müller
 */

public class RunTime2String {

    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static String getTimeDiffString(long miliSec) {
        int days = 0;
        int hours = 0;
        int mins = 0;
        int secs = 0;

        String res = "";
        secs = (int) Math.floor(miliSec/1000.0);
        res = (miliSec-secs*1000)+"ms";
        if (secs>0) {
            mins = (int) Math.floor(secs/60.0);
            res = (secs-mins*60)+"sec, "+res;
            if (mins>0) {
                hours = (int) Math.floor(mins/60.0);
                res = (mins-hours*60)+"min, "+res;
                if (hours>0) {
                    days = (int) Math.floor(hours/24.0);
                    res = (hours-days*24)+"h, "+res;
                }
                if (days>0) {
                    res = days+"day, "+res;
                }
            }
        }

        return res;

    }

    public static String getRuntimeString(Runtime runtime) {
        return ".  RAM: "+(runtime.totalMemory()-runtime.freeMemory())+" bytes"+", Time: "+TIME_FORMATTER.format(System.currentTimeMillis());

    }
}
