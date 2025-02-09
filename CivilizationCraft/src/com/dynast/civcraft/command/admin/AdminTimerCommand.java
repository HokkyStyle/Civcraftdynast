package com.dynast.civcraft.command.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.dynast.civcraft.command.CommandBase;
import com.dynast.civcraft.config.CivSettings;
import com.dynast.civcraft.event.EventTimer;
import com.dynast.civcraft.exception.CivException;
import com.dynast.civcraft.exception.InvalidConfiguration;
import com.dynast.civcraft.main.CivMessage;

public class AdminTimerCommand extends CommandBase {

	@Override
	public void init() {
		command = "/ad timer";
		displayName = CivSettings.localize.localizedString("adcmd_timer_name");
		
		commands.put("set", CivSettings.localize.localizedString("adcmd_timer_setDesc"));
		commands.put("run", CivSettings.localize.localizedString("adcmd_timer_runDesc"));		
	}

	
	public void run_cmd() throws CivException {
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("adcmd_timer_runPrompt"));
		}
		
		EventTimer timer = EventTimer.timers.get(args[1]);
		if (timer == null) {
			throw new CivException(CivSettings.localize.localizedString("var_adcmd_timer_runInvalid",args[1]));
		}
		
		Calendar next;
		try {
			next = timer.getEventFunction().getNextDate();
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			throw new CivException(CivSettings.localize.localizedString("adcmd_timer_runError"));
		}

		timer.getEventFunction().process();
		timer.setLast(EventTimer.getCalendarInServerTimeZone());
		timer.setNext(next);
		timer.save();
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("adcmd_timer_runSuccess"));
	}
	
	public void set_cmd() throws CivException {
		if (args.length < 3) {
			throw new CivException(CivSettings.localize.localizedString("adcmd_timer_setPrompt"));
		}
		
		String timerName = args[1];
		EventTimer timer = EventTimer.timers.get(timerName);
		if (timer == null) {
			throw new CivException( CivSettings.localize.localizedString("var_adcmd_timer_runInvalid",args[1]));
		}
		
		String dateStr = args[2];
		SimpleDateFormat parser = new SimpleDateFormat("d:M:y:H:m");
		
		Calendar next = EventTimer.getCalendarInServerTimeZone();
		try {
			next.setTime(parser.parse(dateStr));
			timer.setNext(next);
			timer.save();
			CivMessage.sendSuccess(sender, "Set timer "+timer.getName()+" to "+parser.format(next.getTime()));
		} catch (ParseException e) {
			throw new CivException(args[2]+CivSettings.localize.localizedString("adcmd_road_setRaidTimeError"));
		}
		
	}
	
	@Override
	public void doDefaultAction() throws CivException {
		showHelp();		
	}

	@Override
	public void showHelp() {
		showBasicHelp();
	}

	@Override
	public void permissionCheck() throws CivException {
		
	}

}
