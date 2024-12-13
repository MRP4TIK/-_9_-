import torch
import os
from torchvision import transforms
from PIL import Image


class RandomGaussianNoise(object):
    """Добавляет случайный гауссов шум к изображению"""

    def __init__(self, mean=0.0, std=0.01):
        self.mean = mean
        self.std = std

    def __call__(self, img):
        if not torch.is_tensor(img):
            img = transforms.ToTensor()(img)
        noise = torch.randn(img.size()) * self.std + self.mean
        img = img + noise
        img = torch.clamp(img, 0., 1.)
        return img


def get_train_transform():
    # Аугментации для обучающей выборки
    return transforms.Compose([
        transforms.ToTensor(),
        transforms.RandomRotation(degrees=10),
        RandomGaussianNoise(mean=0.0, std=0.01),
    ])


def get_val_transform():
    # Для валидации без аугментаций
    return transforms.Compose([
        transforms.ToTensor(),
    ])


class CustomDataset(torch.utils.data.Dataset):
    def __init__(self, images_dir, labels_file_1, labels_file_2, transform=None, train=True, class_to_crop=1, crop_size=224):
        """
        images_dir: путь к папке с изображениями
        labels_file_1: путь к первому файлу меток
        labels_file_2: путь ко второму файлу меток
        transform: трансформации для изображений
        train: флаг, является ли датасет тренировочным
        class_to_crop: класс, для которого применяется кроп
        crop_size: размер кропа
        """
        self.images_dir = images_dir # Текущая папка
        self.transform = transform # get_train_transform для обучающей выборки и get_val_transform для тестовой
        self.train = train
        self.class_to_crop = class_to_crop
        self.crop_size = crop_size

        # Чтение файла с метками
        files = [labels_file_1, labels_file_2]

        labels_str = ""

        for file in files:
            with open(file, 'r') as f:
                labels_str += f.read().strip() + "\n"

        # Определение кол-ва изображений
        num_images = len(labels_str)

        # Списки путей к изображениям и меток класса
        self.image_paths = []
        self.labels = []

        # Сбор изображений и их меток класса
        for i in range(1, num_images + 1):
            img_name = f"img_{i:03d}.jpg"
            img_path = os.path.join(images_dir, img_name)
            if not os.path.isfile(img_path):
                raise FileNotFoundError(f"Не найден файл {img_path}")

            # Преобразование символа '0' или '1' в целое число 0 или 1
            label = int(labels_str[i - 1])
            self.image_paths.append(img_path)
            self.labels.append(label)

    def __getitem__(self, idx):
        image_path = self.image_paths[idx]
        label = self.labels[idx]

        image = Image.open(image_path).convert("RGB")

        # Аугментации применяется только для тренировочной выборки
        if self.train:
            # Случайный кроп для первого класса
            if label == self.class_to_crop:
                crop_transform = transforms.RandomResizedCrop(self.crop_size, scale=(0.8, 1.0))
                image = crop_transform(image)

            if self.transform:
                image = self.transform(image)
        else:
            # Для тестовых данных применяется только transform без кропа
            if self.transform:
                image = self.transform(image)

        return image, label