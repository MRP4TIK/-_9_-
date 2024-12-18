import sys
import torch
import torchvision.transforms as transforms
from torchvision.models import resnet18
from PIL import Image

# Загрузка пути к весам модели
MODEL_PATH = r'-_9_--main\\resnet_18.pth'

# Преобразования для изображения
def transform_image(image_path):
    transform = transforms.Compose([
        transforms.Resize((224, 224)),  # Размер для ResNet18
        transforms.ToTensor(),         # Преобразуем в тензор
    ])
    image = Image.open(image_path).convert('RGB')  # Открываем изображение
    return transform(image).unsqueeze(0)  # Добавляем батч-дименсию

def main():
    if len(sys.argv) < 2:
        print("Ошибка: Не передан путь к изображению.")
        sys.exit(1)

    image_path = sys.argv[1]  # Путь к изображению из аргументов командной строки

    try:
        # Создаём модель ResNet18 с 2 выходами
        model = resnet18(weights=None)
        model.fc = torch.nn.Linear(model.fc.in_features, 2)  # Переопределяем выходной слой для 2 классов

        # Загружаем веса из state_dict
        state_dict = torch.load(MODEL_PATH, map_location=torch.device('cpu'))
        model.load_state_dict(state_dict)  # Загружаем веса в модель

        # Переводим модель в режим оценки
        model.eval()

        # Преобразуем изображение
        input_tensor = transform_image(image_path)

        # Предсказание
        with torch.no_grad():
            outputs = model(input_tensor)
            _, predicted_class = outputs.max(1)

        # Возвращаем результат
        print(predicted_class.item())
    except Exception as e:
        print(f"Ошибка обработки изображения: {e}")

if __name__ == "__main__":
    main()
