importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.channel);

var year;
var month;
var day;
var weekday;
var hour;
var cal;
var date;
var timestamp;

function init() {
	scheduleChange();
}

function scheduleChange() {
	cal = java.util.Calendar.getInstance();
	
	weekday = cal.get(java.util.Calendar.DAY_OF_WEEK);
	hour = cal.get(java.util.Calendar.HOUR_OF_DAY);
	refreshDates(cal);
	
	if (weekday == 1 || weekday == 7) {
		if (hour > 15 && hour < 20) {
			startEvent();
		}
		else if (hour < 16) {
			date = year + "-" + month + "-" + day + " 16:00:00.0";
			timeStamp = java.sql.Timestamp.valueOf(date).getTime();
			setupTask = em.scheduleAtTimestamp("startEvent", timeStamp);
		}
		else if (hour > 19) {
			if (weekday == 7) {
				cal.set(java.util.Calendar.DATE, cal.get(java.util.Calendar.DATE) + 1);
				refreshDates(cal);
				date = year + "-" + month + "-" + day + " 16:00:00.0";
				timeStamp = java.sql.Timestamp.valueOf(date).getTime();
				setupTask = em.scheduleAtTimestamp("startEvent", timeStamp);
			}
			else if (weekday == 1) {
				cal.set(java.util.Calendar.DATE, cal.get(java.util.Calendar.DATE) + 6);
				refreshDates(cal);
				date = year + "-" + month + "-" + day + " 16:00:00.0";
				timeStamp = java.sql.Timestamp.valueOf(date).getTime();
				setupTask = em.scheduleAtTimestamp("startEvent", timeStamp);
			}
		}
	}
	else {
		cal.set(java.util.Calendar.DATE, cal.get(java.util.Calendar.DATE) + (7 - weekday));	
		refreshDates(cal);
		var date = year + "-" + month + "-" + day + " 16:00:00.0";
		var timeStamp = java.sql.Timestamp.valueOf(date).getTime();
		setupTask = em.scheduleAtTimestamp("startEvent", timeStamp);
	}
}

function finishEvent() {
	changeRates(em.getChannelServer().getExpRate() / 1.25);
	em.schedule("resetServerMessage", 1000 * 60 * 2);
	scheduleChange();
}

function startEvent() {
	changeRates(em.getChannelServer().getExpRate() * 1.25);
	refreshDates(cal);
	date = year + "-" + month + "-" + day + " 20:00:00.0";
	timeStamp = java.sql.Timestamp.valueOf(date).getTime();
	setupTask = em.scheduleAtTimestamp("finishEvent", timeStamp);
}

function changeRates(rate) {
	em.getChannelServer().setExpRate(rate);		
	em.getChannelServer().setMesoRate(rate);
	em.getChannelServer().setServerMessage("The experience and meso rates have been changed to x" + rate + " for the event! These will come into effect when you change channel or use !exprate " + rate);
}

function refreshDates(calendar) {
	year = calendar.get(java.util.Calendar.YEAR);
	month = calendar.get(java.util.Calendar.MONTH) + 1;
	if (Math.floor(month / 10) == 0) {
		month = "0" + month;
	}
	day = calendar.get(java.util.Calendar.DATE);
	if (Math.floor(day / 10) == 0) {
		day = "0" + day;
	}
}

function resetServerMessage() {
	em.getChannelServer().setServerMessage("Welcome to MapleSyrup!");
}

function cancelSchedule() {
	setupTask.cancel(true);
}
