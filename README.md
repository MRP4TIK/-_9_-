# 9 Команда
## Определение диких животных на фотографиях

## Описание проекта
Главной целью проекта является разработка системы мониторинга и раннего обнаружения диких животных на фотографиях с использованием технологий компьютерного зрения. Этот проект направлен на повышение безопасности населения и защиту диких животных, таких как тигры и медведи, которые могут заходить в города.

Разработанная система позволяет:
- Определять присутствие диких животных на изображениях с помощью Telegram бота (MVP).
- Минимизировать риски для людей и животных, способствуя гуманному управлению популяциями диких животных.
- Автоматически информировать спасательные службы и местные органы власти о появлении животных (в будущем).

  
## Состав команды (ролевая модель)
- Тимлид: Кондрашов Александр
- Сбор данных: Овчинников Михаил, Можогин Сергей
- Аугментация изображений: Калистратов Александр
- Обучение предобученных нейронных сетей: Кондрашов Александр
- Продакшн: Келарев Михаил
- Ведение документации: Душебаева Айдана

## Основные этапы проекта
### 1.Сбор данных
   - Сбор изображений диких животных и их меток (для классификации).
   - Структурирование данных в формате, удобном для обучения модели.
### 2. Аугментация данных
   - Применение таких методов аугментации, как случайные повороты, добавление шумов и изменение размеров изображений.
### 3. Разделение данных
   - Подготовка данных для обучения, валидации и тестирования.
   - Использование стратифицированного разбиения для равномерного распределения классов.
### 4. Обучение модели
   - Использование предобученных моделей ResNet-18, ResNet-50 и MobileNetV2.
   - Настройка гиперпараметров, таких как learning rate, оптимизаторы и scheduler.
### 5. Оценка эффективности
   - Расчёт метрик (F1-score, accuracy, recall, confusion matrix) для оценки качества модели.
   - Визуализация результатов.
### 6. Внедрение
   - Разработка и тестирование финальной версии системы для реального применения. Интеграция итоговой модели в Telegram.

## Использованные технологии

- Язык программирования: Python, Java
- Фреймворки и библиотеки:
  - PyTorch
  - Torchvision
  - Matplotlib
  - Scikit-learn
  - Seaborn
  - PIL (Pillow)
  - Spring Boot
- Среда разработки: Google Colab

## Установка
### Прежде всего, создаем репозиторий
git clone https://github.com/MRP4TIK/-_9_-
### Далее устанавливаем зависимости
pip install -r requirements.txt
### После чего загружаем данные
Мы поместили изображения в папку Images/ и текстовые файлы с метками (Labels_001-100.txt, Labels_101-200.txt) в корневую директорию проекта.
  
## Применение
### 1. Запуск проекта
Открываем файл обучение_нейросетей.ipynb через Jupyter Notebook или Google Colab, и следуем инструкциям (запускаем код).
### 2. Процесс распознавания
Обучив модель, приступаем к ее использованию и распознаванию диких животных (медведей и тигров) на изображениях:
from PIL import Image
from torchvision import transforms
### Загрузка изображения
image = Image.open("path_to_your_image.jpg").convert("RGB")
### Подготовка изображения
```
transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor()
])
image_tensor = transform(image).unsqueeze(0)
### Предсказание
model.eval()
with torch.no_grad():
    output = model(image_tensor)
    predicted_class = torch.argmax(output).item()

print(f"Результат: {'Животное обнаружено' if predicted_class == 1 else 'Животное отсутствует'}")
```
### Внедрение
Произведена интеграция с Telegram bot: https://t.me/wild_animal_detection_bot
## Полученная структура проекта
wild-animal-detection/
- Папка с изображениями: Images/                 
- Jupyter Notebook с основным кодом: обучение_нейросетей.ipynb
- Список зависимостей: requirements.txt 
- Метки для изображений (1-я часть): Labels_001-100.txt
- Метки для изображений (2-я часть): Labels_101-200.txt
- Документация к проекту: README.md

## Ключевые метрики проекта
После обучения модели были получены следующие результаты:

- Точность (Accuracy): 0.999
- F1-score: 0.999
- Recall: 0.999
 ### Демонстрация результатов с нейронок
 ![Unknown-2](https://github.com/user-attachments/assets/62b2e391-192c-4651-8424-4e4f4c591665)

  ### Матрица ошибок
  ![Unknown-1](https://github.com/user-attachments/assets/c0b54b5f-0377-46fc-b946-35e5d129508c)

