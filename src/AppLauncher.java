public class AppLauncher {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new WeatherAppAPI().setVisible(true));
    }
}
