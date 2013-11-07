package org.osiam.test.util

class DateHelper {
    
    public static Date createDate(int year, int month, int date, int hourOfDay, int minute,
            int second) {
        Calendar calendar = Calendar.getInstance()
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.setTimeZone(TimeZone.getTimeZone(TimeZone.GMT_ID))
        calendar.set(year, month, date, hourOfDay, minute, second)
        calendar.getTime()
    }
            
}
