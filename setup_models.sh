#
# Copyright (c) 2016 Qualcomm Technologies, Inc.
# All Rights Reserved.
# Confidential and Proprietary - Qualcomm Technologies, Inc.
#

#############################################################
# Alexnet setup
#############################################################

mkdir -p inception_v3
mkdir -p inception_v3/images

cd inception_v3

cp -R /home/dashika/Documents/camera-classifiers/inception_v3/data/cropped/*.jpg images
cp -R /home/dashika/Documents/camera-classifiers/inception_v3/dlc/inception_v3.dlc model.dlc
cp -R /home/dashika/Documents/camera-classifiers/inception_v3/data/imagenet_comp_graph_label_strings.txt labels.txt
#cp -R /home/dashika/Downloads/tensorflow-master/tensorflow/contrib/lite/java/demo/inception_v3/data/ilsvrc_2012_mean_cropped.bin mean_image.bin

zip -r inception_v3.zip ./*
mkdir ../app/src/main/res/raw/
cp inception_v3.zip ../app/src/main/res/raw/

cd ..
rm -rf ./inception_v3
