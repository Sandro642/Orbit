package fr.sandro642.orbit.service;

import fr.sandro642.orbit.app.core.Candle;

import java.util.List;

public class IndicatorEngine {

    public void calculateAll(List<Candle> data) {
        if (data.size() < 14) return;
        double gainSum = 0, lossSum = 0;

        for (int i = 0; i < data.size(); i++) {
            // SMA 10
            if (i >= 9) {
                double sum = 0;
                for (int j = 0; j < 10; j++) sum += data.get(i - j).c;
                data.get(i).sma = sum / 10;
            }
            // RSI 14
            if (i > 0) {
                double diff = data.get(i).c - data.get(i - 1).c;
                double gain = Math.max(0, diff);
                double loss = Math.max(0, -diff);
                if (i <= 14) { gainSum += gain; lossSum += loss; }
                else {
                    gainSum = (gainSum * 13 + gain) / 14;
                    lossSum = (lossSum * 13 + loss) / 14;
                    double rs = gainSum / (lossSum == 0 ? 1 : lossSum);
                    data.get(i).rsi = 100 - (100 / (1 + rs));
                }
            }
        }
    }
}
