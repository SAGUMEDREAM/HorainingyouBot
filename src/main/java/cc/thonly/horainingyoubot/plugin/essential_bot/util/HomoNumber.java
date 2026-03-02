package cc.thonly.horainingyoubot.plugin.essential_bot.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomoNumber {

    private static final Map<String, String> NUMS = new HashMap<>();
    private static final List<Integer> NUM_KEYS_DESC = new ArrayList<>();

    private static final Pattern DOT_PATTERN = Pattern.compile("\\.(\\d+?)0*$");

    static {
        Map<Integer, String> int2Str = new HashMap<>();
        int2Str.put(229028, "(114514+114514)");
        int2Str.put(114514, "114514");
        int2Str.put(58596, "114*514");
        int2Str.put(49654, "11*4514");
        int2Str.put(45804, "11451*4");
        int2Str.put(23256, "114*51*4");
        int2Str.put(22616, "11*4*514");
        int2Str.put(19844, "11*451*4");
        int2Str.put(16030, "1145*14");
        int2Str.put(14515, "1+14514");
        int2Str.put(14514, "1*14514");
        int2Str.put(14513, "-1+14514");
        int2Str.put(11455, "11451+4");
        int2Str.put(11447, "11451-4");
        int2Str.put(9028, "(1+1)*4514");
        int2Str.put(8976, "11*4*51*4");
        int2Str.put(7980, "114*5*14");
        int2Str.put(7710, "(1+14)*514");
        int2Str.put(7197, "1+14*514");
        int2Str.put(7196, "1*14*514");
        int2Str.put(7195, "-1+14*514");
        int2Str.put(6930, "11*45*14");
        int2Str.put(6682, "(1-14)*-514");
        int2Str.put(6270, "114*(51+4)");
        int2Str.put(5818, "114*51+4");
        int2Str.put(5810, "114*51-4");
        int2Str.put(5808, "(1+1451)*4");
        int2Str.put(5805, "1+1451*4");
        int2Str.put(5804, "1*1451*4");
        int2Str.put(5803, "-1+1451*4");
        int2Str.put(5800, "(1-1451)*-4");
        int2Str.put(5725, "1145*(1+4)");
        int2Str.put(5698, "11*(4+514)");
        int2Str.put(5610, "-11*(4-514)");
        int2Str.put(5358, "114*(51-4)");
        int2Str.put(5005, "11*(451+4)");
        int2Str.put(4965, "11*451+4");
        int2Str.put(4957, "11*451-4");
        int2Str.put(4917, "11*(451-4)");
        int2Str.put(4584, "(1145+1)*4");
        int2Str.put(4580, "1145*1*4");
        int2Str.put(4576, "(1145-1)*4");
        int2Str.put(4525, "11+4514");
        int2Str.put(4516, "1+1+4514");
        int2Str.put(4515, "1+1*4514");
        int2Str.put(4514, "1-1+4514");
        int2Str.put(4513, "-1*1+4514");
        int2Str.put(4512, "-1-1+4514");
        int2Str.put(4503, "-11+4514");
        int2Str.put(4112, "(1+1)*4*514");
        int2Str.put(3608, "(1+1)*451*4");
        int2Str.put(3598, "(11-4)*514");
        int2Str.put(3435, "-1145*(1-4)");
        int2Str.put(3080, "11*4*5*14");
        int2Str.put(3060, "(11+4)*51*4");
        int2Str.put(2857, "1+14*51*4");
        int2Str.put(2856, "1*14*51*4");
        int2Str.put(2855, "-1+14*51*4");
        int2Str.put(2850, "114*5*(1+4)");
        int2Str.put(2736, "114*(5+1)*4");
        int2Str.put(2652, "(1-14)*51*-4");
        int2Str.put(2570, "1*(1+4)*514");
        int2Str.put(2475, "11*45*(1+4)");
        int2Str.put(2420, "11*4*(51+4)");
        int2Str.put(2280, "114*5*1*4");
        int2Str.put(2248, "11*4*51+4");
        int2Str.put(2240, "11*4*51-4");
        int2Str.put(2166, "114*(5+14)");
        int2Str.put(2068, "11*4*(51-4)");
        int2Str.put(2067, "11+4*514");
        int2Str.put(2058, "1+1+4*514");
        int2Str.put(2057, "1/1+4*514");
        int2Str.put(2056, "1/1*4*514");
        int2Str.put(2055, "-1/1+4*514");
        int2Str.put(2054, "-1-1+4*514");
        int2Str.put(2045, "-11+4*514");
        int2Str.put(2044, "(1+145)*14");
        int2Str.put(2031, "1+145*14");
        int2Str.put(2030, "1*145*14");
        int2Str.put(2029, "-1+145*14");
        int2Str.put(2024, "11*(45+1)*4");
        int2Str.put(2016, "-(1-145)*14");
        int2Str.put(1980, "11*45*1*4");
        int2Str.put(1936, "11*(45-1)*4");
        int2Str.put(1848, "(11+451)*4");
        int2Str.put(1824, "114*(5-1)*4");
        int2Str.put(1815, "11+451*4");
        int2Str.put(1808, "1*(1+451)*4");
        int2Str.put(1806, "1+1+451*4");
        int2Str.put(1805, "1+1*451*4");
        int2Str.put(1804, "1-1+451*4");
        int2Str.put(1803, "1*-1+451*4");
        int2Str.put(1802, "-1-1+451*4");
        int2Str.put(1800, "1*-(1-451)*4");
        int2Str.put(1793, "-11+451*4");
        int2Str.put(1760, "-(11-451)*4");
        int2Str.put(1710, "114*-5*(1-4)");
        int2Str.put(1666, "(114+5)*14");
        int2Str.put(1632, "(1+1)*4*51*4");
        int2Str.put(1542, "1*-(1-4)*514");
        int2Str.put(1526, "(114-5)*14");
        int2Str.put(1485, "11*-45*(1-4)");
        int2Str.put(1456, "1+1451+4");
        int2Str.put(1455, "1*1451+4");
        int2Str.put(1454, "-1+1451+4");
        int2Str.put(1448, "1+1451-4");
        int2Str.put(1447, "1*1451-4");
        int2Str.put(1446, "-1+1451-4");
        int2Str.put(1428, "(11-4)*51*4");
        int2Str.put(1386, "11*(4+5)*14");
        int2Str.put(1260, "(1+1)*45*14");
        int2Str.put(1159, "1145+14");
        int2Str.put(1150, "1145+1+4");
        int2Str.put(1149, "1145+1*4");
        int2Str.put(1148, "1145-1+4");
        int2Str.put(1142, "1145+1-4");
        int2Str.put(1141, "1145-1*4");
        int2Str.put(1140, "(1145-1)-4");
        int2Str.put(1131, "1145-14");
        int2Str.put(1100, "11*4*5*(1+4)");
        int2Str.put(1056, "11*4*(5+1)*4");
        int2Str.put(1050, "(11+4)*5*14");
        int2Str.put(1036, "(1+1)*(4+514)");
        int2Str.put(1026, "114*-(5-14)");
        int2Str.put(1020, "1*(1+4)*51*4");
        int2Str.put(981, "1+14*5*14");
        int2Str.put(980, "1*14*5*14");
        int2Str.put(979, "-1+14*5*14");
        int2Str.put(910, "-(1-14)*5*14");
        int2Str.put(906, "(1+1)*451+4");
        int2Str.put(898, "(1+1)*451-4");
        int2Str.put(894, "(1+1)*(451-4)");
        int2Str.put(880, "11*4*5*1*4");
        int2Str.put(836, "11*4*(5+14)");
        int2Str.put(827, "11+4*51*4");
        int2Str.put(825, "(11+4)*(51+4)");
        int2Str.put(818, "1+1+4*51*4");
        int2Str.put(817, "1*1+4*51*4");
        int2Str.put(816, "1*1*4*51*4");
        int2Str.put(815, "-1+1*4*51*4");
        int2Str.put(814, "-1-1+4*51*4");
        int2Str.put(805, "-11+4*51*4");
        int2Str.put(784, "(11+45)*14");
        int2Str.put(771, "1+14*(51+4)");
        int2Str.put(770, "1*14*(51+4)");
        int2Str.put(769, "(11+4)*51+4");
        int2Str.put(761, "(1+14)*51-4");
        int2Str.put(730, "(1+145)*(1+4)");
        int2Str.put(726, "1+145*(1+4)");
        int2Str.put(725, "1*145*(1+4)");
        int2Str.put(724, "-1-145*-(1+4)");
        int2Str.put(720, "(1-145)*-(1+4)");
        int2Str.put(719, "1+14*51+4");
        int2Str.put(718, "1*14*51+4");
        int2Str.put(717, "-1-14*-51+4");
        int2Str.put(715, "(1-14)*-(51+4)");
        int2Str.put(711, "1+14*51-4");
        int2Str.put(710, "1*14*51-4");
        int2Str.put(709, "-1+14*51-4");
        int2Str.put(705, "(1+14)*(51-4)");
        int2Str.put(704, "11*4*(5-1)*4");
        int2Str.put(688, "114*(5+1)+4");
        int2Str.put(680, "114*(5+1)-4");
        int2Str.put(667, "-(1-14)*51+4");
        int2Str.put(660, "(114+51)*4");
        int2Str.put(659, "1+14*(51-4)");
        int2Str.put(658, "1*14*(51-4)");
        int2Str.put(657, "-1+14*(51-4)");
        int2Str.put(649, "11*(45+14)");
        int2Str.put(644, "1*(1+45)*14");
        int2Str.put(641, "11+45*14");
        int2Str.put(632, "1+1+45*14");
        int2Str.put(631, "1*1+45*14");
        int2Str.put(630, "1*1*45*14");
        int2Str.put(629, "1*-1+45*14");
        int2Str.put(628, "114+514");
        int2Str.put(619, "-11+45*14");
        int2Str.put(616, "1*-(1-45)*14");
        int2Str.put(612, "-1*(1-4)*51*4");
        int2Str.put(611, "(1-14)*-(51-4)");
        int2Str.put(609, "11*(4+51)+4");
        int2Str.put(601, "11*(4+51)-4");
        int2Str.put(595, "(114+5)*(1+4)");
        int2Str.put(584, "114*5+14");
        int2Str.put(581, "1+145*1*4");
        int2Str.put(580, "1*145/1*4");
        int2Str.put(579, "-1+145*1*4");
        int2Str.put(576, "1*(145-1)*4");
        int2Str.put(575, "114*5+1+4");
        int2Str.put(574, "114*5/1+4");
        int2Str.put(573, "114*5-1+4");
        int2Str.put(567, "114*5+1-4");
        int2Str.put(566, "114*5*1-4");
        int2Str.put(565, "114*5-1-4");
        int2Str.put(561, "11/4*51*4");
        int2Str.put(560, "(1+1)*4*5*14");
        int2Str.put(558, "11*4+514");
        int2Str.put(556, "114*5-14");
        int2Str.put(545, "(114-5)*(1+4)");
        int2Str.put(529, "1+14+514");
        int2Str.put(528, "1*14+514");
        int2Str.put(527, "-1+14+514");
        int2Str.put(522, "(1+1)*4+514");
        int2Str.put(521, "11-4+514");
        int2Str.put(520, "1+1+4+514");
        int2Str.put(519, "1+1*4+514");
        int2Str.put(518, "1-1+4+514");
        int2Str.put(517, "-1+1*4+514");
        int2Str.put(516, "-1-1+4+514");
        int2Str.put(514, "(1-1)/4+514");
        int2Str.put(513, "-11*(4-51)-4");
        int2Str.put(512, "1+1-4+514");
        int2Str.put(511, "1*1-4+514");
        int2Str.put(510, "1-1-4+514");
        int2Str.put(509, "11*45+14");
        int2Str.put(508, "-1-1-4+514");
        int2Str.put(507, "-11+4+514");
        int2Str.put(506, "-(1+1)*4+514");
        int2Str.put(502, "11*(45+1)-4");
        int2Str.put(501, "1-14+514");
        int2Str.put(500, "11*45+1+4");
        int2Str.put(499, "11*45*1+4");
        int2Str.put(498, "11*45-1+4");
        int2Str.put(495, "11*(4+5)*(1+4)");
        int2Str.put(492, "11*45+1-4");
        int2Str.put(491, "11*45-1*4");
        int2Str.put(490, "11*45-1-4");
        int2Str.put(488, "11*(45-1)+4");
        int2Str.put(481, "11*45-14");
        int2Str.put(480, "11*(45-1)-4");
        int2Str.put(476, "(114+5)/1*4");
        int2Str.put(470, "-11*4+514");
        int2Str.put(466, "11+451+4");
        int2Str.put(460, "114*(5-1)+4");
        int2Str.put(458, "11+451-4");
        int2Str.put(457, "1+1+451+4");
        int2Str.put(456, "1*1+451+4");
        int2Str.put(455, "1-1+451+4");
        int2Str.put(454, "-1+1*451+4");
        int2Str.put(453, "-1-1+451+4");
        int2Str.put(452, "114*(5-1)-4");
        int2Str.put(450, "(1+1)*45*(1+4)");
        int2Str.put(449, "1+1+451-4");
        int2Str.put(448, "1+1*451-4");
        int2Str.put(447, "1/1*451-4");
        int2Str.put(446, "1*-1+451-4");
        int2Str.put(445, "-1-1+451-4");
        int2Str.put(444, "-11+451+4");
        int2Str.put(440, "(1+1)*4*(51+4)");
        int2Str.put(438, "(1+145)*-(1-4)");
        int2Str.put(436, "-11+451-4");
        int2Str.put(435, "-1*145*(1-4)");
        int2Str.put(434, "-1-145*(1-4)");
        int2Str.put(432, "(1-145)*(1-4)");
        int2Str.put(412, "(1+1)*4*51+4");
        int2Str.put(404, "(1+1)*4*51-4");
        int2Str.put(400, "-114+514");
        int2Str.put(396, "11*4*-(5-14)");
        int2Str.put(385, "(11-4)*(51+4)");
        int2Str.put(376, "(1+1)*4*(51-4)");
        int2Str.put(375, "(1+14)*5*(1+4)");
        int2Str.put(368, "(1+1)*(45+1)*4");
        int2Str.put(363, "(1+1451)/4");
        int2Str.put(361, "(11-4)*51+4");
        int2Str.put(360, "(1+1)*45*1*4");
        int2Str.put(357, "(114+5)*-(1-4)");
        int2Str.put(353, "(11-4)*51-4");
        int2Str.put(352, "(1+1)*(45-1)*4");
        int2Str.put(351, "1+14*-5*-(1+4)");
        int2Str.put(350, "1*(1+4)*5*14");
        int2Str.put(349, "-1+14*5*(1+4)");
        int2Str.put(341, "11*(45-14)");
        int2Str.put(337, "1-14*-(5+1)*4");
        int2Str.put(336, "1*14*(5+1)*4");
        int2Str.put(335, "-1+14*(5+1)*4");
        int2Str.put(329, "(11-4)*(51-4)");
        int2Str.put(327, "-(114-5)*(1-4)");
        int2Str.put(325, "-(1-14)*5*(1+4)");
        int2Str.put(318, "114+51*4");
        int2Str.put(312, "(1-14)*-(5+1)*4");
        int2Str.put(300, "(11+4)*5/1*4");
        int2Str.put(297, "-11*(4+5)*(1-4)");
        int2Str.put(291, "11+4*5*14");
        int2Str.put(286, "(1145-1)/4");
        int2Str.put(285, "(11+4)*(5+14)");
        int2Str.put(282, "1+1+4*5*14");
        int2Str.put(281, "1+14*5/1*4");
        int2Str.put(280, "1-1+4*5*14");
        int2Str.put(279, "1*-1+4*5*14");
        int2Str.put(278, "-1-1+4*5*14");
        int2Str.put(275, "1*(1+4)*(51+4)");
        int2Str.put(270, "(1+1)*45*-(1-4)");
        int2Str.put(269, "-11+4*5*14");
        int2Str.put(268, "11*4*(5+1)+4");
        int2Str.put(267, "1+14*(5+14)");
        int2Str.put(266, "1*14*(5+14)");
        int2Str.put(265, "-1+14*(5+14)");
        int2Str.put(260, "1*(14+51)*4");
        int2Str.put(259, "1*(1+4)*51+4");
        int2Str.put(257, "(1+1)/4*514");
        int2Str.put(252, "(114-51)*4");
        int2Str.put(251, "1*-(1+4)*-51-4");
        int2Str.put(248, "11*4+51*4");
        int2Str.put(247, "-(1-14)*(5+14)");
        int2Str.put(240, "(11+4)*(5-1)*4");
        int2Str.put(236, "11+45*(1+4)");
        int2Str.put(235, "1*(1+4)*(51-4)");
        int2Str.put(234, "11*4*5+14");
        int2Str.put(231, "11+4*(51+4)");
        int2Str.put(230, "1*(1+45)*(1+4)");
        int2Str.put(229, "1145/(1+4)");
        int2Str.put(227, "1+1+45*(1+4)");
        int2Str.put(226, "1*1+45*(1+4)");
        int2Str.put(225, "11*4*5+1+4");
        int2Str.put(224, "11*4*5/1+4");
        int2Str.put(223, "11*4*5-1+4");
        int2Str.put(222, "1+1+4*(51+4)");
        int2Str.put(221, "1/1+4*(51+4)");
        int2Str.put(220, "1*1*(4+51)*4");
        int2Str.put(219, "1+14+51*4");
        int2Str.put(218, "1*14+51*4");
        int2Str.put(217, "11*4*5+1-4");
        int2Str.put(216, "11*4*5-1*4");
        int2Str.put(215, "11*4*5-1-4");
        int2Str.put(214, "-11+45*(1+4)");
        int2Str.put(212, "(1+1)*4+51*4");
        int2Str.put(211, "11-4+51*4");
        int2Str.put(210, "1+1+4+51*4");
        int2Str.put(209, "1+1*4*51+4");
        int2Str.put(208, "1*1*4+51*4");
        int2Str.put(207, "-1+1*4*51+4");
        int2Str.put(206, "11*4*5-14");
        int2Str.put(204, "(1-1)/4+51*4");
        int2Str.put(202, "1+1-4+51*4");
        int2Str.put(201, "1/1-4+51*4");
        int2Str.put(200, "1/1*4*51-4");
        int2Str.put(199, "1*-1+4*51-4");
        int2Str.put(198, "-1-1+4*51-4");
        int2Str.put(197, "-11+4+51*4");
        int2Str.put(196, "-(1+1)*4+51*4");
        int2Str.put(195, "(1-14)*5*(1-4)");
        int2Str.put(192, "(1+1)*4*(5+1)*4");
        int2Str.put(191, "1-14+51*4");
        int2Str.put(190, "1*-14+51*4");
        int2Str.put(189, "-11-4+51*4");
        int2Str.put(188, "1-1-(4-51)*4");
        int2Str.put(187, "1/-1+4*(51-4)");
        int2Str.put(186, "1+1+(45+1)*4");
        int2Str.put(185, "1-1*-(45+1)*4");
        int2Str.put(184, "114+5*14");
        int2Str.put(183, "-1+1*(45+1)*4");
        int2Str.put(182, "1+1+45/1*4");
        int2Str.put(181, "1+1*45*1*4");
        int2Str.put(180, "1*1*45*1*4");
        int2Str.put(179, "-1/1+45*1*4");
        int2Str.put(178, "-1-1+45*1*4");
        int2Str.put(177, "1+1*(45-1)*4");
        int2Str.put(176, "1*1*(45-1)*4");
        int2Str.put(175, "-1+1*(45-1)*4");
        int2Str.put(174, "-1-1+(45-1)*4");
        int2Str.put(172, "11*4*(5-1)-4");
        int2Str.put(171, "114*(5+1)/4");
        int2Str.put(170, "(11-45)*-(1+4)");
        int2Str.put(169, "114+51+4");
        int2Str.put(168, "(11+45)*-(1-4)");
        int2Str.put(165, "11*-45/(1-4)");
        int2Str.put(161, "114+51-4");
        int2Str.put(160, "1+145+14");
        int2Str.put(159, "1*145+14");
        int2Str.put(158, "-1+145+14");
        int2Str.put(157, "1*(1-4)*-51+4");
        int2Str.put(154, "11*(4-5)*-14");
        int2Str.put(152, "(1+1)*4*(5+14)");
        int2Str.put(151, "1+145+1+4");
        int2Str.put(150, "1+145*1+4");
        int2Str.put(149, "1*145*1+4");
        int2Str.put(148, "1*145-1+4");
        int2Str.put(147, "-1+145-1+4");
        int2Str.put(146, "11+45*-(1-4)");
        int2Str.put(143, "1+145+1-4");
        int2Str.put(142, "1+145*1-4");
        int2Str.put(141, "1+145-1-4");
        int2Str.put(140, "1*145-1-4");
        int2Str.put(139, "-1+145-1-4");
        int2Str.put(138, "-1*(1+45)*(1-4)");
        int2Str.put(137, "1+1-45*(1-4)");
        int2Str.put(136, "1*1-45*(1-4)");
        int2Str.put(135, "-1/1*45*(1-4)");
        int2Str.put(134, "114+5/1*4");
        int2Str.put(133, "114+5+14");
        int2Str.put(132, "1+145-14");
        int2Str.put(131, "1*145-14");
        int2Str.put(130, "-1+145-14");
        int2Str.put(129, "114+5*-(1-4)");
        int2Str.put(128, "1+1+(4+5)*14");
        int2Str.put(127, "1-14*(5-14)");
        int2Str.put(126, "1*(14-5)*14");
        int2Str.put(125, "-1-14*(5-14)");
        int2Str.put(124, "114+5+1+4");
        int2Str.put(123, "114-5+14");
        int2Str.put(122, "114+5-1+4");
        int2Str.put(121, "11*(45-1)/4");
        int2Str.put(120, "-(1+1)*4*5*(1-4)");
        int2Str.put(118, "(1+1)*(45+14)");
        int2Str.put(117, "(1-14)*(5-14)");
        int2Str.put(116, "114+5+1-4");
        int2Str.put(115, "114+5*1-4");
        int2Str.put(114, "11*4+5*14");
        int2Str.put(113, "114-5/1+4");
        int2Str.put(112, "114-5-1+4");
        int2Str.put(111, "11+4*5*(1+4)");
        int2Str.put(110, "-(11-451)/4");
        int2Str.put(107, "11-4*-(5+1)*4");
        int2Str.put(106, "114-5+1-4");
        int2Str.put(105, "114+5-14");
        int2Str.put(104, "114-5-1-4");
        int2Str.put(103, "11*(4+5)+1*4");
        int2Str.put(102, "11*(4+5)-1+4");
        int2Str.put(101, "1+1*4*5*(1+4)");
        int2Str.put(100, "1*(1+4)*5*1*4");
        int2Str.put(99, "11*4+51+4");
        int2Str.put(98, "1+1+4*(5+1)*4");
        int2Str.put(97, "1+1*4*(5+1)*4");
        int2Str.put(96, "11*(4+5)+1-4");
        int2Str.put(95, "114-5-14");
        int2Str.put(94, "114-5/1*4");
        int2Str.put(93, "(1+1)*45-1+4");
        int2Str.put(92, "(1+1)*(45-1)+4");
        int2Str.put(91, "11*4+51-4");
        int2Str.put(90, "-114+51*4");
        int2Str.put(89, "(1+14)*5+14");
        int2Str.put(88, "1*14*(5+1)+4");
        int2Str.put(87, "11+4*(5+14)");
        int2Str.put(86, "(1+1)*45*1-4");
        int2Str.put(85, "1+14+5*14");
        int2Str.put(84, "1*14+5*14");
        int2Str.put(83, "-1+14+5*14");
        int2Str.put(82, "1+1+4*5/1*4");
        int2Str.put(81, "1/1+4*5*1*4");
        int2Str.put(80, "1-1+4*5*1*4");
        int2Str.put(79, "1*-1+4*5/1*4");
        int2Str.put(78, "(1+1)*4+5*14");
        int2Str.put(77, "11-4+5*14");
        int2Str.put(76, "1+1+4+5*14");
        int2Str.put(75, "1+14*5*1+4");
        int2Str.put(74, "1/1*4+5*14");
        int2Str.put(73, "1*14*5-1+4");
        int2Str.put(72, "-1-1+4+5*14");
        int2Str.put(71, "(1+14)*5-1*4");
        int2Str.put(70, "11+45+14");
        int2Str.put(69, "1*14+51+4");
        int2Str.put(68, "1+1-4+5*14");
        int2Str.put(67, "1-1*4+5*14");
        int2Str.put(66, "1*14*5-1*4");
        int2Str.put(65, "1*14*5-1-4");
        int2Str.put(64, "11*4+5*1*4");
        int2Str.put(63, "11*4+5+14");
        int2Str.put(62, "1+14+51-4");
        int2Str.put(61, "1+1+45+14");
        int2Str.put(60, "11+45*1+4");
        int2Str.put(59, "114-51-4");
        int2Str.put(58, "-1+1*45+14");
        int2Str.put(57, "1+14*5-14");
        int2Str.put(56, "1*14*5-14");
        int2Str.put(55, "-1+14*5-14");
        int2Str.put(54, "11-4+51-4");
        int2Str.put(53, "11+45+1-4");
        int2Str.put(52, "11+45/1-4");
        int2Str.put(51, "11+45-1-4");
        int2Str.put(50, "1+1*45/1+4");
        int2Str.put(49, "1*1*45/1+4");
        int2Str.put(48, "-11+45+14");
        int2Str.put(47, "1/-1+45-1+4");
        int2Str.put(46, "11*4+5+1-4");
        int2Str.put(45, "11+4*5+14");
        int2Str.put(44, "114-5*14");
        int2Str.put(43, "1+1*45+1-4");
        int2Str.put(42, "11+45-14");
        int2Str.put(41, "1/1*45*1-4");
        int2Str.put(40, "-11+4*51/4");
        int2Str.put(39, "-11+45+1+4");
        int2Str.put(38, "-11+45*1+4");
        int2Str.put(37, "-11+45-1+4");
        int2Str.put(36, "11+4*5+1+4");
        int2Str.put(35, "11*4+5-14");
        int2Str.put(34, "1-14+51-4");
        int2Str.put(33, "1+1+45-14");
        int2Str.put(32, "1*1+45-14");
        int2Str.put(31, "1/1*45-14");
        int2Str.put(30, "1*-1+45-14");
        int2Str.put(29, "-11+45-1-4");
        int2Str.put(28, "11+4*5+1-4");
        int2Str.put(27, "11+4*5/1-4");
        int2Str.put(26, "11-4+5+14");
        int2Str.put(25, "11*4-5-14");
        int2Str.put(24, "1+14-5+14");
        int2Str.put(23, "1*14-5+14");
        int2Str.put(22, "1*14+5-1+4");
        int2Str.put(21, "-1-1+4+5+14");
        int2Str.put(20, "-11+45-14");
        int2Str.put(19, "1+1+4*5+1-4");
        int2Str.put(18, "1+1+4*5*1-4");
        int2Str.put(17, "11+4*5-14");
        int2Str.put(16, "11-4-5+14");
        int2Str.put(15, "1+14-5+1+4");
        int2Str.put(14, "11+4-5/1+4");
        int2Str.put(13, "1*14-5/1+4");
        int2Str.put(12, "-11+4+5+14");
        int2Str.put(11, "11*-4+51+4");
        int2Str.put(10, "-11/4+51/4");
        int2Str.put(9, "11-4+5+1-4");
        int2Str.put(8, "11-4+5/1-4");
        int2Str.put(7, "11-4+5-1-4");
        int2Str.put(6, "1-14+5+14");
        int2Str.put(5, "11-4*5+14");
        int2Str.put(4, "-11-4+5+14");
        int2Str.put(3, "11*-4+51-4");
        int2Str.put(2, "-11+4-5+14");
        int2Str.put(1, "11/(45-1)*4");
        int2Str.put(0, "(1-1)*4514");
        for (var mapEntry : int2Str.entrySet()) {
            NUMS.put(mapEntry.getKey().toString(), mapEntry.getValue());
        }
        NUMS.put("⑨", "11-4-5+1-4");

        for (String key : NUMS.keySet()) {
            if (key.matches("\\d+")) {
                int v = Integer.parseInt(key);
                if (v > 0) NUM_KEYS_DESC.add(v);
            }
        }

        NUM_KEYS_DESC.sort(Collections.reverseOrder());
    }

    private static Integer getMinDiv(long num) {
        for (int v : NUM_KEYS_DESC) {
            if (num >= v) return v;
        }
        return null;
    }

    private static String demolish(double num) {
        if (Double.isInfinite(num) || Double.isNaN(num)) {
            return "这么恶臭的" + num + "有必要论证吗";
        }

        if (num < 0) {
            return "(⑨)*(" + demolish(-num) + ")"
                    .replaceAll("\\*\\(1\\)", "");
        }

        if (Math.floor(num) != num) {
            String fixed = String.format(Locale.US, "%.16f", num);
            Matcher m = DOT_PATTERN.matcher(fixed);
            int n = m.find() ? m.group(1).length() : 0;
            return "(" + demolish(num * Math.pow(10, n)) + ")/(10)^(" + n + ")";
        }

        long n = (long) num;

        if (NUMS.containsKey(String.valueOf(n))) {
            return String.valueOf(n);
        }

        Integer div = getMinDiv(n);
        if (div == null) return String.valueOf(n);

        long a = n / div;
        long b = n % div;

        String expr = div + "*(" + demolish(a) + ")+(" + demolish(b) + ")";
        return expr.replaceAll("\\*\\(1\\)|\\+\\(0\\)$", "");
    }

    private static String finisher(String expr) {

        for (Map.Entry<String, String> e : NUMS.entrySet()) {
            expr = expr.replace(e.getKey(), e.getValue());
        }

        expr = expr.replace("^", "**");

        Pattern p1 = Pattern.compile("([*/])\\(([^+\\-()]+)\\)");
        while (p1.matcher(expr).find()) {
            expr = expr.replaceAll("([*/])\\(([^+\\-()]+)\\)", "$1$2");
        }

        Pattern p2 = Pattern.compile("([+\\-])\\(([^()]+)\\)([+\\-)])");
        while (p2.matcher(expr).find()) {
            expr = expr.replaceAll("([+\\-])\\(([^()]+)\\)([+\\-)])", "$1$2$3");
        }

        Pattern p3 = Pattern.compile("([+\\-])\\(([^()]+)\\)$");
        while (p3.matcher(expr).find()) {
            expr = expr.replaceAll("([+\\-])\\(([^()]+)\\)$", "$1$2");
        }

        if (expr.matches("^\\([^()]+\\)$")) {
            expr = expr.replaceAll("^\\(([^()]+)\\)$", "$1");
        }

        return expr.replace("+-", "-");
    }

    public static String homo(double num) {
        return finisher(demolish(num));
    }
}
