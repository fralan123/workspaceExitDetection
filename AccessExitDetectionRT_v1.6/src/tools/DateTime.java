package tools;

import java.util.Calendar;

public class DateTime {
    
	Calendar cal;
    String  date = null, day = null, month = null, time = null, hour = null, min = null, sec = null;
    
    public String Today(){
    	cal = Calendar.getInstance();
    	
    	switch(cal.get(Calendar.DAY_OF_MONTH)){
	    	case 0:
	    		day = "00";
	    		break;
	    		
	    	case 1:
	    		day = "01";
	    		break;
	    		
	    	case 2:
	    		day = "02";
	    		break;
	    		
	    	case 3:
	    		day = "03";
	    		break;
	    		
	    	case 4:
	    		day = "04";
	    		break;
	    		
	    	case 5:
	    		day = "05";
	    		break;
	    		
	    	case 6:
	    		day = "06";
	    		break;
	    		
	    	case 7:
	    		day = "07";
	    		break;
	    		
	    	case 8:
	    		day = "08";
	    		break;
	    		
	    	case 9:
	    		day = "09";
	    		break;
	    		
	    	default:
	    		day = cal.get(Calendar.DAY_OF_MONTH) + "";
	    		break;
	    }
    	
    	switch(cal.get(Calendar.MONTH)){    		
    		case 0:
	    		month = "01";
	    		break;
    		
    		case 1:
	    		month = "02";
	    		break;
	    		
	    	case 2:
	    		month = "03";
	    		break;
	    		
	    	case 3:
	    		month = "04";
	    		break;
	    		
	    	case 4:
	    		month = "05";
	    		break;
	    		
	    	case 5:
	    		month = "06";
	    		break;
	    		
	    	case 6:
	    		month = "07";
	    		break;
	    		
	    	case 7:
	    		month = "08";
	    		break;
	    		
	    	case 8:
	    		month = "09";
	    		break;
                    case 9:
	    		month = "10";
	    		break;
	    		
	    	case 10:
	    		month = "11";
	    		break;
                    case 11:
	    		month = "12";
	    		break;
	    		
	    	default:
	    		day = cal.get(Calendar.DAY_OF_MONTH) + "";
	    		break;
    	}
    	date = "2012"+"-"+month+"-"+day;
	    return date;
    }
    
    public String Now(){
    	cal = Calendar.getInstance();
    	
    	switch(cal.get(Calendar.HOUR_OF_DAY)){
	    	case 0:
	    		hour = "00";
	    		break;
	    		
	    	case 1:
	    		hour = "01";
	    		break;
	    		
	    	case 2:
	    		hour = "02";
	    		break;
	    		
	    	case 3:
	    		hour = "03";
	    		break;
	    		
	    	case 4:
	    		hour = "04";
	    		break;
	    		
	    	case 5:
	    		hour = "05";
	    		break;
	    		
	    	case 6:
	    		hour = "06";
	    		break;
	    		
	    	case 7:
	    		hour = "07";
	    		break;
	    		
	    	case 8:
	    		hour = "08";
	    		break;
	    		
	    	case 9:
	    		hour = "09";
	    		break;
	    		
	    	default:
	    		hour = cal.get(Calendar.HOUR_OF_DAY) + "";
	    		break;
	    }
    	
    	switch(cal.get(Calendar.MINUTE)){
	    	case 0:
	    		min = "00";
	    		break;
	    		
	    	case 1:
	    		min = "01";
	    		break;
	    		
	    	case 2:
	    		min = "02";
	    		break;
	    		
	    	case 3:
	    		min = "03";
	    		break;
	    		
	    	case 4:
	    		min = "04";
	    		break;
	    		
	    	case 5:
	    		min = "05";
	    		break;
	    		
	    	case 6:
	    		min = "06";
	    		break;
	    		
	    	case 7:
	    		min = "07";
	    		break;
	    		
	    	case 8:
	    		min = "08";
	    		break;
	    		
	    	case 9:
	    		min = "09";
	    		break;
	    		
	    	default:
	    		min = cal.get(Calendar.MINUTE) + "";
	    		break;
    	}
    	
    	switch(cal.get(Calendar.SECOND)){
	    	case 0:
	    		sec = "00";
	    		break;
	    		
	    	case 1:
	    		sec = "01";
	    		break;
	    		
	    	case 2:
	    		sec = "02";
	    		break;
	    		
	    	case 3:
	    		sec = "03";
	    		break;
	    		
	    	case 4:
	    		sec = "04";
	    		break;
	    		
	    	case 5:
	    		sec = "05";
	    		break;
	    		
	    	case 6:
	    		sec = "06";
	    		break;
	    		
	    	case 7:
	    		sec = "07";
	    		break;
	    		
	    	case 8:
	    		sec = "08";
	    		break;
	    		
	    	case 9:
	    		sec = "09";
	    		break;
	    		
	    	default:
	    		sec = cal.get(Calendar.SECOND) + "";
	    		break;
		}
    	//Conformamos la hora del dia.
    	time = hour + min + sec;
    	
    	return time;    
    }

}
