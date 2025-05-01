package Group2BankSystem;

import Group2BankSystem.model.AccountManager;
import Group2BankSystem.ui.MainFrame;

import javax.swing.*;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });

        scheduleMonthlyInterest();
    }

    private static void scheduleMonthlyInterest() {
        Timer timer = new Timer(true);
        Calendar firstRun = Calendar.getInstance();
        firstRun.add(Calendar.MONTH, 1);
        firstRun.set(Calendar.DAY_OF_MONTH, 1);
        firstRun.set(Calendar.HOUR_OF_DAY, 3);
        firstRun.set(Calendar.MINUTE, 0);
        firstRun.set(Calendar.SECOND, 0);
        firstRun.set(Calendar.MILLISECOND, 0);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    AccountManager.applyMonthlyInterest();
                } catch (Exception e) {
                    System.err.println("Error applying interest: " + e.getMessage());
                }
            }
        }, firstRun.getTime(), TimeUnit.DAYS.toMillis(31));
    }
}
