import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WeatherAppAPI extends JFrame {
    private JSONObject weatherData;
    private JPanel forecastPanel; // Biến toàn cục

    public WeatherAppAPI() {
        super("Weather App");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(600, 850);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setResizable(false);
        this.addGuiComponents();
    }

    private void addGuiComponents() {
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 45);
        searchTextField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        this.add(searchTextField);

        JLabel weatherConditionImage = new JLabel();
        weatherConditionImage.setBounds(100, 100, 450, 217);
        weatherConditionImage.setHorizontalTextPosition(SwingConstants.CENTER);
        this.add(weatherConditionImage);

        JLabel temperatureText = new JLabel("--°C");
        temperatureText.setBounds(0, 320, 600, 54);
        temperatureText.setFont(new Font("Segoe UI", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(temperatureText);

        JLabel weatherConditionDesc = new JLabel("---");
        weatherConditionDesc.setBounds(0, 370, 600, 36);
        weatherConditionDesc.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(weatherConditionDesc);

        JLabel humidityImage = new JLabel(loadImage("src/Images/humidity.png"));
        humidityImage.setBounds(15, 430, 74, 66);
        this.add(humidityImage);

        JLabel humidityText = new JLabel("<html><b>Humidity</b> ---%</html>");
        humidityText.setBounds(90, 430, 85, 55);
        humidityText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        this.add(humidityText);

        JLabel windspeedImage = new JLabel(loadImage("src/Images/windspeed.png"));
        windspeedImage.setBounds(220, 430, 74, 66);
        this.add(windspeedImage);

        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> --km/h</html>");
        windspeedText.setBounds(310, 430, 85, 55);
        windspeedText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        this.add(windspeedText);

        // forecastPanel: tạo trước, để update sau
        forecastPanel = new JPanel();
        forecastPanel.setBounds(15, 520, 550, 280);
        forecastPanel.setLayout(new GridLayout(1, 5, 10, 0));
        forecastPanel.setBackground(Color.WHITE);
        this.add(forecastPanel);

        JButton searchButton = new JButton("🔍");
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.setBackground(new Color(56, 142, 60));
        searchButton.setForeground(Color.WHITE);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchTextField.getText().trim();
                if (userInput.isEmpty()) return;

                weatherData = WeatherApp.getWeatherData(userInput);
                JSONArray forecastData = WeatherApp.getHourlyForecast(userInput);

                if (weatherData == null || forecastData == null) {
                    JOptionPane.showMessageDialog(null, "Không thể lấy dữ liệu. Vui lòng thử lại.");
                    return;
                }

                // Update ảnh thời tiết hiện tại
                String weatherCondition = (String) weatherData.get("weather_condition");
                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/Images/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/Images/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/Images/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/Images/snow.png"));
                        break;
                }

                temperatureText.setText(weatherData.get("temperature") + "°C");
                weatherConditionDesc.setText(weatherCondition);
                humidityText.setText("<html><b>Humidity</b> " + weatherData.get("humidity") + "%</html>");
                windspeedText.setText("<html><b>Windspeed</b> " + weatherData.get("wind_speed") + "km/h</html>");

                // Cập nhật dự báo 5 ngày
                forecastPanel.removeAll();

                for (int i = 0; i < 5; i++) {
                    JSONObject dayForecast = (JSONObject) forecastData.get(i);

                    JPanel dayCard = new JPanel();
                    dayCard.setLayout(new BoxLayout(dayCard, BoxLayout.Y_AXIS));
                    dayCard.setBackground(new Color(240, 240, 240));
                    dayCard.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                    dayCard.setAlignmentX(Component.CENTER_ALIGNMENT);

                    JLabel dateLabel = new JLabel((String) dayForecast.get("date"));
                    dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    dayCard.add(dateLabel);

                    String condition = (String) dayForecast.get("weather");
                    JLabel iconLabel = new JLabel();
                    iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    switch (condition) {
                        case "Clear": iconLabel.setIcon(loadImage("src/Images/clear.png")); break;
                        case "Cloudy": iconLabel.setIcon(loadImage("src/Images/cloudy.png")); break;
                        case "Rain": iconLabel.setIcon(loadImage("src/Images/rain.png")); break;
                        case "Snow": iconLabel.setIcon(loadImage("src/Images/snow.png")); break;
                    }
                    dayCard.add(iconLabel);

                    double temp = (double) dayForecast.get("temperature");
                    JLabel tempLabel = new JLabel(temp + "°C");
                    tempLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    dayCard.add(tempLabel);

                    forecastPanel.add(dayCard);
                }

                forecastPanel.revalidate();
                forecastPanel.repaint();
            }
        });
        this.add(searchButton);
    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
