package forward.enums;

/**
 * 设备类型枚举
 *
 * @author Administrator
 *
 */
public enum ParamTypeEnum {

	ZRD("zrd", "针入度(25℃,100g,5s)"),

	ZRD1("zrd", "针入度(25℃,100g,5s)(0.1mm)"),
// 2
	ZRDPI("zrdPI", "针入度指数PI"),
// 11
	RHD("rhd", "软化点(环球法) (℃)"),

	YD("yd","延度(5℃)(cm)"),
// 6
	YD10("yd10", "延度(10℃)(cm)"),
// 7
	YD15("yd15", "延度(15℃)(cm)"),
// 4
	DLND("dlnd", "60℃动力粘度(Pa.s)"),

	YDND("ydnd", "135℃动力粘度(Pa.s)"),
// 12
	LHL("lhl", "蜡含量(蒸馏法)(%)"),
// 5
	SD("sd", "闪点(COC)(℃)"),
// 10
	RJD("rjd", "溶解度(三氯乙烯)(%)"),
// 8
	MD("md", "密度(15℃)(g/cm3)"),
// 14
	ZLBH("zlbh", "质量变化(%)"),

	CLZRDB("clzrdb", "残留针入度比(25℃)(%)"),
// 13
	CLZRDB1("clzrdb", "残留针入度比(25℃)(mm)"),

	CLYD("clyd","残留延度(5℃)(cm)"),
// 3
	CLYD10("clyd10", "残留延度(10℃)(cm)"),
// 1
	CLYD15("clyd15", "残留延度(15℃)(cm)"),
// 9
	SHRP("shrp", "SHRP计划沥青材料"),

	TXHF("txhf", "弹性恢复(25℃)(%)"),

	CCWDXLX("ccwdxlx", "储存稳定性离析(℃)"),

	SBSGXJ("sbsgxj", "SBS改性剂掺加量(%)");


	private String code;

	private String name;

	private ParamTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static String getNameByCode(String code) {
		for (ParamTypeEnum type : ParamTypeEnum.values()) {
			if (type.getCode().equals(code) ) {
				return type.getName();
			}
		}
		return null;
	}

}
