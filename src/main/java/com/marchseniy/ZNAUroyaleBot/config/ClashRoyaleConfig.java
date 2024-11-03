package com.marchseniy.ZNAUroyaleBot.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.*;

@Configuration
@Data
@PropertySource("application.properties")
public class ClashRoyaleConfig {
    private static ClashRoyaleConfig instance;

    @Value("${royale.token}")
    private String token;

    @Value("${royale.clan.tag}")
    private String clanTag;

    private final List<String> whiteListTags = Arrays.asList(
            "#GYLQ9ULCQ", // mr.Danik
            "#J209RPQ8Q", // ZR I tankist14
            "#VGR90Y88L", // Marchseniy
            "#UC0LQ9L8G", // ЯБЛОЧКО[YT]
            "#YCL2U88C9" // ZR I топ4ik
    );

    private final List<Fact> facts = new ArrayList<>()
    {{
        add(new Fact("Илкка очень редко посещает офис Supercell, ему больше по душе чиллить в Дубае и трахать там телок.",
                "img/ilkka_dubai.png"));
        add(new Fact("Золотой рыцарь - внебрачный сын Баскова.",
                "img/baskov.png"));
        add(new Fact("Когда поражений подряд становится слишком много (больше 4-х), знай, ты в тильте - дальше только хуже. " +
                "Прекрати играть, отойди отдохнуть и через некоторое время возвращайся снова с новыми силами.",
                "img/tilt.png"));
        add(new Fact("Раньше все шутили, что Илкка - пидр, но в текущих реалиях это действительно так. Комания Supercell ежегодно отмечает гей парад.",
                "img/ilkka_gay.png"));
        add(new Fact("Илкка - давний фанат Баскова. Он вдохновился им и сам лично создал модель для новой карты, которая основывалась на его внешности.",
                "img/ilkka_make_baskov.png"));
    }};

    private final Map<String, String> chestNames = new HashMap<>()
    {{
        put("Tower Troop Chest", "Башенный сундук");
        put("Silver Chest", "Серебряный сундук");
        put("Golden Chest", "Золотой сундук");
        put("Magical Chest", "Магический сундук");
        put("Giant Chest", "Гигантский сундук");
        put("Legendary Chest", "Легендарный сундук");
        put("Mega Lightning Chest", "Сундук с мега-молнией");
        put("Epic Chest", "Эпический сундук");
        put("Gold Crate", "Золотой ящик");
        put("Plentiful Gold Crate", "Обильный золотой ящик");
        put("Overflowing Gold Crate", "Переполненный золотой ящик");
        put("Royal Wild Chest", "Королевский дикий сундук");
    }};

    private final Map<String, String> cardNames = new HashMap<>()
    {{
        put("Knight", "Рыцарь");
        put("Archers", "Стрелы");
        put("Goblins", "Гоблины");
        put("Giant", "Гигант");
        put("P.E.K.K.A", "П.E.К.К.А");
        put("Minions", "Миньоны");
        put("Balloon", "Шар");
        put("Witch", "Ведьма");
        put("Barbarians", "Варвары");
        put("Golem", "Голем");
        put("Skeletons", "Скелеты");
        put("Valkyrie", "Валькирия");
        put("Skeleton Army", "Армия скелетов");
        put("Bomber", "Бомбардировщик");
        put("Musketeer", "Мушкетер");
        put("Baby Dragon", "Дракончик");
        put("Prince", "Принц");
        put("Wizard", "Колдун");
        put("Mini P.E.K.K.A", "Мини П.E.К.К.А");
        put("Spear Goblins", "Гоблины-копейщики");
        put("Giant Skeleton", "Гигантский скелет");
        put("Hog Rider", "Всадник на кабане");
        put("Minion Horde", "Орда миньонов");
        put("Ice Wizard", "Ледяной колдун");
        put("Royal Giant", "Королевский гигант");
        put("Guards", "Стражи");
        put("Princess", "Принцесса");
        put("Dark Prince", "Темный принц");
        put("Three Musketeers", "Три мушкетера");
        put("Lava Hound", "Адская гончая");
        put("Ice Spirit", "Ледяной дух");
        put("Fire Spirit", "Огненный дух");
        put("Miner", "Шахтёр");
        put("Sparky", "Спарки");
        put("Bowler", "Вышибала");
        put("Lumberjack", "Дровосек");
        put("Battle Ram", "Боевой таран");
        put("Inferno Dragon", "Пламенный дракон");
        put("Ice Golem", "Ледяной голем");
        put("Mega Minion", "Мегаминьон");
        put("Dart Goblin", "Гоблин с дротиками");
        put("Goblin Gang", "Гоблинская банда");
        put("Electro Wizard", "Громовержец");
        put("Elite Barbarians", "Элитные варвары");
        put("Hunter", "Охотник");
        put("Executioner", "Палач");
        put("Bandit", "Бандитка");
        put("Royal Recruits", "Королевские рекруты");
        put("Night Witch", "Ночная ведьма");
        put("Bats", "Летучие мыши");
        put("Royal Ghost", "Королевский призрак");
        put("Ram Rider", "Всадник на баране");
        put("Zappies", "Электрический дух");
        put("Rascals", "Разбойники");
        put("Cannon Cart", "Повозка с пушкой");
        put("Mega Knight", "Мегарыцарь");
        put("Skeleton Barrel", "Скелетная бочка");
        put("Flying Machine", "Летающая машина");
        put("Wall Breakers", "Стенобои");
        put("Royal Hogs", "Королевские кабаны");
        put("Goblin Giant", "Гоблин-гигант");
        put("Fisherman", "Рыбак");
        put("Magic Archer", "Магический лучник");
        put("Electro Dragon", "Электродракон");
        put("Firecracker", "Ракетчица");
        put("Mighty Miner", "Шустрый Шахтёр");
        put("Elixir Golem", "Эликсирный голем");
        put("Battle Healer", "Целительница");
        put("Skeleton King", "Король скелетов");
        put("Archer Queen", "Королева лучниц");
        put("Golden Knight", "Золотой рыцарь");
        put("Monk", "Монах");
        put("Skeleton Dragons", "Скелетные драконы");
        put("Mother Witch", "Ведьмина бабушка");
        put("Electro Spirit", "Электроический дух");
        put("Electro Giant", "Электрогигант");
        put("Phoenix", "Феникс");
        put("Little Prince", "Маленький принц");
        put("Goblin Demolisher", "Гоблин-подрывник");
        put("Goblin Machine", "Гоблинская машина");
        put("Suspicious Bush", "Подозрительный куст");
        put("Goblinstein", "Гоблинштейн");
        put("Cannon", "Пушка");
        put("Goblin Hut", "Хижина гоблинов");
        put("Mortar", "Мортира");
        put("Inferno Tower", "Адская башня");
        put("Bomb Tower", "Башня-бомбежка");
        put("Barbarian Hut", "Хижина варваров");
        put("Tesla", "Тесла");
        put("Elixir Collector", "Сборщик эликсира");
        put("X-Bow", "Арбалет");
        put("Tombstone", "Надгробие");
        put("Furnace", "Печь");
        put("Goblin Cage", "Клетка с гоблином");
        put("Goblin Drill", "Гоблинский бур");
        put("Fireball", "Огненный шар");
        put("Arrows", "Стрелы");
        put("Rage", "Ярость");
        put("Rocket", "Ракета");
        put("Goblin Barrel", "Гоблинская бочка");
        put("Freeze", "Заморозка");
        put("Mirror", "Зеркало");
        put("Lightning", "Молния");
        put("Zap", "Разряд");
        put("Poison", "Яд");
        put("Graveyard", "Кладбище");
        put("The Log", "Бревно");
        put("Tornado", "Торнадо");
        put("Clone", "Клон");
        put("Earthquake", "Землетрясение");
        put("Barbarian Barrel", "Бочка с варварами");
        put("Heal Spirit", "Дух исцеления");
        put("Giant Snowball", "Гигантский снежок");
        put("Royal Delivery", "Королевская почта");
        put("Void", "Бездна");
        put("Goblin Curse", "Проклятие гоблинов");
    }};

    @AllArgsConstructor
    @Getter
    public static class Fact {
        private String text;
        private String imagePath;
    }
}
