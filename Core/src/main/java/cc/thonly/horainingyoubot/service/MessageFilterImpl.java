package cc.thonly.horainingyoubot.service;


// 如果你在网络直播请停止你预览此代码的行为，，，直播间挨封了别找我，，，

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class MessageFilterImpl {
    /**
     * 对外入口
     */
    public boolean isAllowContent(String input) {
        if (input == null || input.isBlank()) {
            return true;
        }

        String normalized = normalize(input);

        for (String word : BLACK_LIST) {
            String target = normalize(word);

            // 快速直匹配
            if (normalized.contains(target)) {
                return false;
            }

            // 拆字绕过检测
            if (matchWithGap(normalized, target)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 顺序匹配 + 最大间隔限制
     */
    private boolean matchWithGap(String text, String word) {
        int lastIndex = -1;

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            int index = findNext(text, ch, lastIndex + 1);

            if (index == -1 || index - lastIndex > MAX_GAP + 1) {
                return false;
            }
            lastIndex = index;
        }
        return true;
    }

    private int findNext(String text, char target, int from) {
        for (int i = from; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 文本归一化
     * 非常关键（防绕过）
     */
    private String normalize(String input) {
        // Unicode 标准化（全角转半角等）
        String s = Normalizer.normalize(input, Normalizer.Form.NFKC);

        // 小写化
        s = s.toLowerCase(Locale.ROOT);

        // 去除空白符
        s = s.replaceAll("\\s+", "");

        // 去除常见分隔符
        s = s.replaceAll("[\\p{Punct}_·•…—-]", "");

        return s;
    }

    public Set<String> getWords() {
        return Collections.unmodifiableSet(BLACK_LIST);
    }

























































































    private static final int MAX_GAP = 3;
    //region 直播保护区（不要展开）
    private static final Set<String> BLACK_LIST = new HashSet<>(Set.of(
            "习近平", "庆丰包子铺", "xjp", "建制派", "保守派", "自由派", "左派", "右派", "键政", "键正", "言论自由", "自由言论", "新闻自由", "自由新闻", "民主自由", "自由民主", "人民", "工团", "联合工团", "起义", "反攻大陆", "反共", "台独", "台独运", "台湾独立", "红色幽灵", "赵紫阳", "共产联盟", "社会主义联盟", "工会", "镇压", "反攻", "裸体", "R18", "R-18", "R-18-G", "R18G", "安那其", "安人", "人民共和国", "香港独立", "反送中", "反送钟", "牧田", "目田", "小粉红", "小品客", "极端民族", "极端军国", "特色纳粹主义", "特色独裁主义", "特色民族主义", "独裁", "罢免", "中华人民共和国", "中华民国", "北洋政府", "北洋", "解放军", "反革命", "滞了", "支了", "支那了", "支那人", "Shina", "Shinese", "修正主义", "xzzy", "民族主义", "mzzy", "法西斯", "法西斯主义", "土共", "共匪", "tg", "中华共和国", "集权", "极权", "布尔什维克", "水晶棺", "tian安门", "天an门", "天安men", "统一中国", "统一台湾", "解放台湾", "不同意的请", "反动", "狗粮", "国府", "政府", "国民政府", "攻台", "共军", "台军", "台湾共和国", "台湾国", "希特勒", "习特勒", "徐特勒", "香蕉皮", "xijinping", "徐继平", "CCP", "CPC", "中国共产党", "之春", "坦克人", "坦克壬", "坦克仁", "TANK", "TANK_MAN", "TANK-MAN", "TANK MAN", "大日本帝国", "大日本皇军", "皇军", "小日子", "小日本", "帝国皇军", "一亿玉碎", "伪满洲国", "满洲国", "清政府", "青虫", "ChingChong", "chingchong", "请虫", "清虫", "Chink", "Chinaman", "Chineseman", "Zhina", "Zhinese", "zhina", "zhinese", "抗美", "抗共", "Taiwan", "Taiwanese", "文化大革命", "文革", "滞纳", "支那", "指哪", "紫蜡烛", "芝麻猪", "芝麻人", "大革命", "戏剧埤", "邓小平", "天安门", "腊肉", "恶俗狗", "胡耀邦", "特色社会主义", "民主", "6489", "下台", "毛泽东", "习仲勋", "胡景涛", "蔡英文", "中共", "二共", "特色修正主义", "习朝", "习皇", "改革开放", "中国特色修正主义", "社会主义", "社h主义", "共产主义", "共c主义", "江泽民", "共chan主义", "特色党", "特色共", "蒋介石", "恶俗", "包子", "乳包", "乳华", "乳化", "包包", "维尼", "为你", "喂你", "违逆", "皇帝", "皇上", "温家宝", "薄熙来", "周恩来", "王洪文", "林彪", "江青", "法轮功", "四人帮", "革命", "白纸", "8964", "1989", "19890604"
    ));
    //endregion
}
