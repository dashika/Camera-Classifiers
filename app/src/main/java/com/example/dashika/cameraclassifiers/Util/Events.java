package com.example.dashika.cameraclassifiers.Util;

import com.example.dashika.cameraclassifiers.Model.SNPEModel;
import com.qualcomm.qti.snpe.NeuralNetwork;

import java.util.Set;

public class Events {
    private Events() {
    }

    public static class onClassificationResult {
        String[] labels;

        public onClassificationResult(String[] labels) {
            this.labels = labels;
        }

        public String[] getLabels() {
            return labels;
        }
    }


    public static class onModelLoaded {
        final Set<SNPEModel> snpeModels;

        public onModelLoaded(Set<SNPEModel> snpeModels) {
            this.snpeModels = snpeModels;
        }

        public Set<SNPEModel> getSNPEModel() {
            return snpeModels;
        }
    }


    public static class onNetworkLoaded {
        final NeuralNetwork neuralNetwork;

        public onNetworkLoaded(NeuralNetwork neuralNetwork) {
            this.neuralNetwork = neuralNetwork;
        }

        public NeuralNetwork getNeuralNetwork() {
            return neuralNetwork;
        }
    }

}
