package features.predictability;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;


/**
 * This class contains ExtractionModule code that detects selected Trigraphs and extracts their durations.
 * <p><p>
 * TrigraphDuration only detects normal trigraphs which are defined as
 * 3 consecutive press, release KeyEvents. This ignores trigraphs typed
 * with modifier keys like shift, ignores trigraphs that contain negative
 * intervals (Slurs) and ignores trigraphs that contain negative intervals
 * due to spaces or punctuation occuring at the beginning or end of the 
 * trigraph.
 * 
 * Trigraph duration is defined as the time between press of the first key and the press of the last key.
 * @author Patrick
 */
public class TrigraphDuration extends Predictability implements ExtractionModule {

	private static final String modelName = "SESSION1";
	private static final String gramType = "keystroke";
	private Set<String> trigraphs;
	private HashMap<String, LinkedList<Double>> featureMap = new HashMap<String, LinkedList<Double>>(1800);
		
	public TrigraphDuration () {
		super(modelName,gramType);
		String[] trigraphs = {"ABA", "ABI", "ABL", "ABO", "ABS", "ACA", "ACC", "ACE", "ACH", "ACI", "ACK", "ACO", "ACR", "ACT", "ACU", "ADA", "ADD", "ADE", "ADI", "ADN", "ADO", "ADS", "ADU", "ADV", "ADY", "AFE", "AFF", "AFR", "AFT", "AGA", "AGE", "AGI", "AGO", "AGR", "AHA", "AHO", "AHT", "AID", "AIL", "AIN", "AIR", "AIS", "AIT", "AJO", "AKE", "AKI", "ALA", "ALC", "ALE", "ALI", "ALK", "ALL", "ALM", "ALO", "ALR", "ALS", "ALT", "ALU", "ALW", "ALY", "AMA", "AMB", "AME", "AMI", "AMO", "AMP", "AMS", "AMU", "ANA", "ANB", "ANC", "AND", "ANE", "ANF", "ANG", "ANH", "ANI", "ANK", "ANM", "ANN", "ANO", "ANS", "ANT", "ANY", "AOL", "APA", "APE", "API", "APO", "APP", "APR", "APS", "APT", "ARA", "ARC", "ARD", "ARE", "ARG", "ARI", "ARK", "ARL", "ARM", "ARN", "ARO", "ARR", "ARS", "ART", "ARY", "ASD", "ASE", "ASH", "ASI", "ASK", "ASL", "ASN", "ASO", "ASP", "ASS", "AST", "ASU", "ASY", "ATA", "ATC", "ATE", "ATH", "ATI", "ATL", "ATN", "ATO", "ATR", "ATS", "ATT", "ATU", "ATY", "AUG", "AUS", "AUT", "AUY", "AVA", "AVE", "AVI", "AVO", "AWA", "AWE", "AYB", "AYE", "AYI", "AYS", "AYT", "AYU", "AZI", "AZY", "AZZ", "BAB", "BAC", "BAD", "BAL", "BAN", "BAR", "BAS", "BAT", "BEA", "BEC", "BED", "BEE", "BEF", "BEG", "BEH", "BEI", "BEL", "BEN", "BER", "BES", "BET", "BIG", "BIL", "BIN", "BIR", "BIT", "BJE", "BLA", "BLE", "BLI", "BLO", "BLU", "BLY", "BMA", "BNE", "BOA", "BOD", "BOO", "BOR", "BOS", "BOT", "BOU", "BOW", "BOX", "BOY", "BRA", "BRE", "BRI", "BRO", "BSI", "BSO", "BST", "BUI", "BUL", "BUS", "BUT", "BUY", "BVE", "CAL", "CAM", "CAN", "CAP", "CAR", "CAS", "CAT", "CAU", "CCA", "CCE", "CCI", "CCO", "CCU", "CEA", "CEB", "CED", "CEE", "CEI", "CEL", "CEN", "CEP", "CER", "CES", "CHA", "CHE", "CHI", "CHN", "CHO", "CHR", "CHU", "CIA", "CID", "CIE", "CIF", "CIL", "CIN", "CIP", "CIR", "CIS", "CIT", "CIV", "CKE", "CKG", "CKI", "CKL", "CKS", "CLA", "CLE", "CLI", "CLO", "CLU", "COA", "COH", "COL", "COM", "CON", "COO", "COP", "COR", "COS", "COU", "COV", "COW", "CRA", "CRE", "CRI", "CRO", "CRU", "CRY", "CTE", "CTI", "CTL", "CTO", "CTR", "CTS", "CTU", "CUA", "CUL", "CUM", "CUR", "CUS", "CUT", "DAD", "DAG", "DAI", "DAL", "DAM", "DAN", "DAR", "DAT", "DAY", "DDE", "DDI", "DDL", "DEA", "DEC", "DED", "DEE", "DEF", "DEL", "DEM", "DEN", "DEO", "DEP", "DER", "DES", "DET", "DEV", "DGE", "DIA", "DIC", "DID", "DIE", "DIF", "DIG", "DIN", "DIR", "DIS", "DIT", "DIV", "DLE", "DLY", "DNT", "DOC", "DOE", "DOG", "DOI", "DOL", "DOM", "DON", "DOO", "DOW", "DRA", "DRE", "DRI", "DRO", "DRU", "DSC", "DUA", "DUC", "DUE", "DUR", "DVE", "EAC", "EAD", "EAK", "EAL", "EAM", "EAN", "EAR", "EAS", "EAT", "EAU", "EAV", "EBA", "EBM", "EBO", "EBS", "ECA", "ECC", "ECE", "ECH", "ECI", "ECK", "ECO", "ECT", "ECU", "EDE", "EDG", "EDI", "EDN", "EDS", "EDU", "EDY", "EED", "EEE", "EEI", "EEK", "EEL", "EEM", "EEN", "EEP", "EER", "EES", "EET", "EFE", "EFF", "EFI", "EFO", "EFT", "EFU", "EGA", "EGE", "EGI", "EHI", "EIG", "EIN", "EIR", "EIT", "EIV", "EKE", "EKS", "ELA", "ELD", "ELE", "ELF", "ELI", "ELL", "ELO", "ELP", "ELS", "ELT", "ELV", "ELY", "EMA", "EMB", "EME", "EMI", "EMO", "EMP", "EMS", "ENA", "ENC", "END", "ENE", "ENG", "ENH", "ENI", "ENJ", "ENM", "ENN", "ENO", "ENR", "ENS", "ENT", "ENV", "EOL", "EON", "EOP", "EOS", "EPA", "EPE", "EPI", "EPL", "EPO", "EPS", "EPT", "EQU", "ERA", "ERB", "ERC", "ERD", "ERE", "ERF", "ERG", "ERH", "ERI", "ERL", "ERM", "ERN", "ERO", "ERP", "ERR", "ERS", "ERT", "ERV", "ERW", "ERY", "ESA", "ESC", "ESE", "ESH", "ESI", "ESN", "ESO", "ESP", "ESS", "EST", "ETA", "ETB", "ETC", "ETE", "ETH", "ETI", "ETO", "ETR", "ETS", "ETT", "ETW", "ETY", "EVA", "EVE", "EVI", "EVO", "EWA", "EWE", "EWS", "EXA", "EXC", "EXE", "EXI", "EXP", "EXT", "EYB", "EYE", "FAC", "FAI", "FAL", "FAM", "FAN", "FAR", "FAS", "FAT", "FAV", "FEA", "FEC", "FEE", "FEL", "FEN", "FER", "FES", "FEW", "FFE", "FFI", "FFO", "FIC", "FIE", "FIG", "FIL", "FIN", "FIR", "FIS", "FIT", "FIV", "FIX", "FLI", "FLO", "FLY", "FOC", "FOL", "FOO", "FOR", "FOU", "FRE", "FRI", "FRO", "FTB", "FTE", "FUC", "FUL", "FUN", "FUR", "FUS", "FUT", "GAI", "GAM", "GAN", "GAR", "GAT", "GED", "GEN", "GER", "GES", "GET", "GGE", "GGL", "GHL", "GHO", "GHT", "GIC", "GIN", "GIR", "GIV", "GLA", "GLE", "GMA", "GNE", "GNI", "GOD", "GOE", "GOI", "GOL", "GON", "GOO", "GOR", "GOS", "GOT", "GRA", "GRE", "GRI", "GRO", "GRY", "GUA", "GUE", "GUI", "GUL", "GUY", "GYM", "HAB", "HAD", "HAI", "HAL", "HAM", "HAN", "HAP", "HAR", "HAS", "HAT", "HAV", "HEA", "HEC", "HED", "HEE", "HEI", "HEL", "HEM", "HEN", "HER", "HES", "HET", "HEW", "HEY", "HGE", "HIC", "HIE", "HIG", "HIK", "HIL", "HIM", "HIN", "HIO", "HIP", "HIR", "HIS", "HIT", "HJE", "HLE", "HLY", "HOD", "HOE", "HOI", "HOL", "HOM", "HON", "HOO", "HOP", "HOR", "HOS", "HOT", "HOU", "HOW", "HRE", "HRI", "HRO", "HTE", "HTS", "HUG", "HUM", "HUN", "HUR", "HUS", "HUT", "HYS", "IAL", "IAN", "IAT", "IBE", "IBI", "IBL", "ICA", "ICE", "ICH", "ICI", "ICK", "ICS", "ICT", "ICU", "IDA", "IDD", "IDE", "IDI", "IDN", "IDS", "IDU", "IEC", "IED", "IEK", "IEL", "IEN", "IER", "IES", "IET", "IEV", "IEW", "IFE", "IFF", "IFI", "IFT", "IFU", "IGA", "IGG", "IGH", "IGI", "IGN", "IGU", "IIN", "IKE", "ILA", "ILD", "ILE", "ILI", "ILL", "ILS", "ILT", "ILY", "IMA", "IME", "IMI", "IMM", "IMP", "INA", "INB", "INC", "IND", "INE", "INF", "ING", "INI", "INJ", "INK", "INL", "INM", "INN", "INO", "INS", "INT", "INU", "INV", "IOL", "ION", "IOR", "IOT", "IOU", "IPA", "IPL", "IPS", "IQU", "IRC", "IRD", "IRE", "IRL", "IRO", "IRR", "IRS", "IRT", "ISA", "ISC", "ISE", "ISH", "ISI", "ISL", "ISN", "ISO", "ISP", "ISS", "IST", "ISU", "ITA", "ITC", "ITE", "ITH", "ITI", "ITL", "ITO", "ITS", "ITT", "ITU", "ITY", "IUN", "IUS", "IUT", "IVA", "IVE", "IVI", "IZA", "IZE", "JAC", "JAZ", "JEC", "JOB", "JOR", "JOY", "JUD", "JUM", "JUN", "JUR", "JUS", "KED", "KEE", "KEL", "KEM", "KEN", "KEP", "KER", "KES", "KET", "KEY", "KGR", "KID", "KIL", "KIN", "KIT", "KNE", "KNO", "LAB", "LAC", "LAD", "LAG", "LAH", "LAI", "LAK", "LAN", "LAR", "LAS", "LAT", "LAU", "LAX", "LAY", "LCO", "LDE", "LDH", "LDI", "LDN", "LDR", "LEA", "LEC", "LED", "LEE", "LEF", "LEG", "LEI", "LEM", "LEN", "LER", "LES", "LET", "LEV", "LEY", "LIA", "LIC", "LID", "LIE", "LIF", "LIG", "LIK", "LIM", "LIN", "LIO", "LIS", "LIT", "LIV", "LIZ", "LKE", "LKI", "LLA", "LLE", "LLI", "LLL", "LLO", "LLS", "LLY", "LMO", "LOA", "LOC", "LOD", "LOG", "LOI", "LON", "LOO", "LOR", "LOS", "LOT", "LOU", "LOV", "LOW", "LPE", "LPS", "LRE", "LSE", "LSO", "LTH", "LTI", "LTU", "LUA", "LUD", "LUE", "LUS", "LUT", "LUY", "LVE", "LWA", "LYI", "LYM", "LYR", "LYU", "LYW", "MAD", "MAG", "MAI", "MAJ", "MAK", "MAL", "MAN", "MAR", "MAS", "MAT", "MAY", "MAZ", "MBE", "MBL", "MBU", "MEA", "MEB", "MED", "MEE", "MEL", "MEM", "MEN", "MEO", "MER", "MES", "MET", "MEW", "MFO", "MIC", "MID", "MIG", "MIL", "MIN", "MIS", "MIT", "MIX", "MMA", "MME", "MMI", "MMM", "MMO", "MMU", "MOD", "MOM", "MON", "MOO", "MOR", "MOS", "MOT", "MOU", "MOV", "MPA", "MPE", "MPI", "MPL", "MPO", "MPR", "MPT", "MPU", "MSE", "MUC", "MUL", "MUN", "MUR", "MUS", "MUY", "MYS", "NAG", "NAL", "NAM", "NAN", "NAR", "NAS", "NAT", "NBE", "NBO", "NCE", "NCH", "NCI", "NCL", "NCO", "NCY", "NDA", "NDE", "NDI", "NDL", "NDO", "NDS", "NDU", "NDY", "NEA", "NEC", "NED", "NEE", "NEG", "NEI", "NEL", "NER", "NES", "NET", "NEV", "NEW", "NEX", "NEY", "NFE", "NFL", "NFO", "NFU", "NGA", "NGE", "NGI", "NGL", "NGR", "NGS", "NGU", "NHA", "NIA", "NIC", "NIE", "NIF", "NIG", "NIM", "NIN", "NIO", "NIS", "NIT", "NIV", "NIZ", "NJO", "NJU", "NKE", "NKI", "NKS", "NLI", "NLY", "NME", "NNA", "NNE", "NNI", "NNO", "NNY", "NOI", "NOL", "NON", "NOO", "NOR", "NOT", "NOU", "NOW", "NOY", "NRE", "NSE", "NSH", "NSI", "NST", "NSU", "NSW", "NTA", "NTE", "NTH", "NTI", "NTL", "NTO", "NTR", "NTS", "NTU", "NTY", "NUE", "NUM", "NUR", "NUT", "NVE", "NVI", "NVO", "NYO", "NYT", "OAC", "OAD", "OAR", "OAT", "OBA", "OBB", "OBE", "OBJ", "OBL", "OBV", "OCA", "OCC", "OCE", "OCI", "OCK", "OCT", "OCU", "ODA", "ODE", "ODS", "ODU", "ODY", "OES", "OFE", "OFF", "OFI", "OFT", "OGE", "OGI", "OGL", "OGR", "OGY", "OHO", "OIC", "OIL", "OIN", "OIR", "OIS", "OIT", "OIU", "OJE", "OKE", "OKI", "OKS", "OLD", "OLE", "OLF", "OLI", "OLK", "OLL", "OLO", "OLS", "OLU", "OLV", "OLY", "OMA", "OME", "OMI", "OMM", "OMP", "OMU", "ONA", "ONC", "OND", "ONE", "ONF", "ONG", "ONI", "ONL", "ONM", "ONN", "ONO", "ONS", "ONT", "ONV", "OOD", "OOG", "OOK", "OOL", "OOM", "OON", "OOO", "OOR", "OOS", "OOT", "OPE", "OPI", "OPL", "OPP", "OPR", "OPS", "OPT", "OPU", "ORA", "ORC", "ORD", "ORE", "ORG", "ORI", "ORK", "ORL", "ORM", "ORN", "ORO", "ORP", "ORR", "ORS", "ORT", "ORY", "OSE", "OSI", "OSP", "OSS", "OST", "OTA", "OTB", "OTE", "OTH", "OTI", "OTM", "OTO", "OTS", "OTT", "OUC", "OUD", "OUG", "OUI", "OUL", "OUN", "OUP", "OUR", "OUS", "OUT", "OUY", "OVE", "OVI", "OWA", "OWB", "OWE", "OWI", "OWL", "OWN", "OWS", "OXI", "OYE", "OYF", "OYI", "OYS", "PAC", "PAG", "PAI", "PAL", "PAM", "PAN", "PAP", "PAR", "PAS", "PAT", "PAY", "PEA", "PEC", "PED", "PEE", "PEL", "PEN", "PEO", "PER", "PES", "PET", "PHO", "PHY", "PIC", "PID", "PIE", "PIN", "PIR", "PIS", "PIT", "PLA", "PLE", "PLI", "PLO", "PLU", "PLY", "POE", "POI", "POK", "POL", "PON", "POO", "POP", "POR", "POS", "POT", "POU", "POW", "PPA", "PPE", "PPI", "PPL", "PPO", "PPR", "PPY", "PRA", "PRE", "PRI", "PRO", "PSE", "PTI", "PUL", "PUN", "PUR", "PUS", "PUT", "QUA", "QUE", "QUI", "RAB", "RAC", "RAD", "RAG", "RAI", "RAL", "RAM", "RAN", "RAP", "RAR", "RAS", "RAT", "RAV", "RAW", "RAY", "RAZ", "RBA", "RCE", "RCH", "RCI", "RCO", "RDE", "RDI", "RDS", "REA", "REC", "RED", "REE", "REF", "REG", "REI", "REL", "REM", "REN", "REP", "REQ", "RER", "RES", "RET", "REV", "REW", "RFA", "RFE", "RFO", "RFU", "RGA", "RGE", "RHY", "RIA", "RIB", "RIC", "RID", "RIE", "RIF", "RIG", "RIL", "RIM", "RIN", "RIO", "RIP", "RIS", "RIT", "RIV", "RKD", "RKE", "RKI", "RKS", "RLD", "RLE", "RLS", "RLY", "RMA", "RME", "RMI", "RMS", "RNA", "RNE", "RNI", "ROA", "ROB", "ROC", "ROD", "ROF", "ROG", "ROI", "ROJ", "ROK", "ROL", "ROM", "RON", "ROO", "ROP", "ROR", "ROS", "ROT", "ROU", "ROV", "ROW", "RPO", "RRA", "RRE", "RRI", "RRO", "RRY", "RSA", "RSE", "RSI", "RSO", "RST", "RTA", "RTE", "RTH", "RTI", "RTM", "RTS", "RTU", "RTY", "RUB", "RUC", "RUE", "RUG", "RUL", "RUM", "RUN", "RUS", "RUT", "RVE", "RVI", "RYB", "RYD", "RYI", "RYO", "RYT", "SAD", "SAF", "SAG", "SAI", "SAL", "SAM", "SAN", "SAP", "SAR", "SAS", "SAT", "SAV", "SAW", "SAY", "SCA", "SCE", "SCH", "SCI", "SCO", "SCR", "SCU", "SEA", "SEB", "SEC", "SED", "SEE", "SEL", "SEM", "SEN", "SEP", "SER", "SES", "SET", "SEV", "SEX", "SFU", "SHA", "SHE", "SHI", "SHO", "SIA", "SIB", "SIC", "SID", "SIE", "SIG", "SIL", "SIM", "SIN", "SIO", "SIS", "SIT", "SIV", "SIX", "SIZ", "SKE", "SKI", "SKY", "SLA", "SLE", "SLI", "SLO", "SLY", "SMA", "SME", "SMI", "SNE", "SNO", "SNT", "SOC", "SOF", "SOI", "SOL", "SOM", "SON", "SOO", "SOR", "SOU", "SPA", "SPE", "SPI", "SPO", "SPR", "SSA", "SSE", "SSF", "SSI", "SSO", "SSS", "SSU", "STA", "STE", "STI", "STL", "STM", "STO", "STR", "STS", "STU", "STY", "SUA", "SUB", "SUC", "SUE", "SUM", "SUN", "SUP", "SUR", "SUS", "SWE", "SWI", "SYS", "TAB", "TAC", "TAG", "TAH", "TAI", "TAK", "TAL", "TAN", "TAR", "TAS", "TAT", "TAY", "TBA", "TCH", "TEA", "TEC", "TED", "TEE", "TEH", "TEL", "TEM", "TEN", "TEP", "TER", "TES", "TEV", "TEX", "THA", "THC", "THE", "THG", "THI", "THJ", "THL", "THM", "THO", "THR", "THS", "THT", "THY", "TIA", "TIC", "TIE", "TIF", "TIG", "TIL", "TIM", "TIN", "TIO", "TIP", "TIR", "TIS", "TIT", "TIU", "TIV", "TLE", "TLY", "TMA", "TME", "TOD", "TOG", "TOI", "TOL", "TOM", "TON", "TOO", "TOP", "TOR", "TOT", "TOU", "TOW", "TRA", "TRE", "TRI", "TRO", "TRU", "TRY", "TSI", "TTA", "TTE", "TTI", "TTL", "TTO", "TTR", "TTY", "TUA", "TUD", "TUF", "TUN", "TUP", "TUR", "TWA", "TWE", "TWI", "TWO", "TYH", "TYL", "TYP", "UAG", "UAL", "UAR", "UAS", "UAT", "UBJ", "UBL", "UBS", "UCC", "UCH", "UCK", "UCT", "UDD", "UDE", "UDI", "UDL", "UDS", "UEN", "UES", "UET", "UFF", "UGE", "UGG", "UGH", "UGS", "UIC", "UIL", "UIN", "UIR", "UIS", "UIT", "ULA", "ULD", "ULE", "ULL", "ULT", "ULY", "UMA", "UMB", "UME", "UMM", "UMP", "UMS", "UNA", "UNC", "UND", "UNF", "UNG", "UNI", "UNK", "UNL", "UNN", "UNS", "UNT", "UPE", "UPI", "UPL", "UPP", "UPS", "URA", "URC", "URD", "URE", "URI", "URN", "URP", "URR", "URS", "URT", "URV", "URY", "USA", "USC", "USE", "USH", "USI", "USL", "USS", "UST", "USU", "UTA", "UTE", "UTH", "UTI", "UTS", "UTT", "UTU", "UTY", "UYS", "VAC", "VAI", "VAL", "VAN", "VAR", "VAS", "VAT", "VCE", "VED", "VEI", "VEL", "VEM", "VEN", "VER", "VES", "VEY", "VIC", "VID", "VIE", "VIL", "VIN", "VIO", "VIR", "VIS", "VIT", "VIV", "VOI", "VOL", "VOR", "WAI", "WAL", "WAN", "WAR", "WAS", "WAT", "WAY", "WEA", "WEB", "WED", "WEE", "WEL", "WEN", "WER", "WES", "WEV", "WHA", "WHE", "WHI", "WHO", "WHY", "WIC", "WID", "WIH", "WIL", "WIM", "WIN", "WIS", "WIT", "WLE", "WNS", "WOI", "WOL", "WOM", "WON", "WOO", "WOR", "WOU", "WRA", "WRI", "WRO", "XAC", "XAM", "XAS", "XCE", "XCI", "XEC", "XED", "XPE", "XPL", "XPR", "XTR", "YAH", "YAR", "YBA", "YBE", "YBO", "YEA", "YED", "YEL", "YER", "YES", "YET", "YFR", "YIN", "YLE", "YON", "YOR", "YOU", "YPE", "YPI", "YRI", "YSE", "YSI", "YSP", "YST", "YTH", "YTI", "YWH", "YWO", "ZED", "ZIN"};
		this.trigraphs = new TreeSet<String>(Arrays.asList(trigraphs));
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		featureMap.clear();
		for (String s : trigraphs)
			featureMap.put(s, new LinkedList<Double>());
		LinkedList<KeyStroke> buffer = new LinkedList<KeyStroke>();
		String stringBuffer = new String();
		for (Answer a : data) {
			for (KeyStroke k : a.getKeyStrokeList()) {
			/////////////////Main Logic///////////////////////
				buffer.add(k);
				if (buffer.size() > 6) {
					buffer.poll();
					stringBuffer = keyStrokesToString(buffer).toUpperCase();
					if (trigraphs.contains(stringBuffer) && isNormalTrigraph(buffer)) {
						KeyStroke ks1 = buffer.getFirst();
						KeyStroke ks2 = buffer.get(2);
						KeyStroke ks3 = buffer.get(4);
						featureMap.get(stringBuffer).add((ks3.getWhen() - ks1.getWhen())/1000.0/keyStrokeTrigramModel.getTrigramProbability(ks1, ks2, ks3));
					}
				}
			/////////////////////////////////////////////////
			}
		}
		LinkedList<Feature> output = new LinkedList<Feature>();
		for (String s : trigraphs) {
			output.add(new Feature("TD_Predict_" + s, featureMap.get(s)));
		}
//		for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}

	@Override
	public String getName() {
		return "Trigraph Duration";
	}
	
	/**
	 * Simple Method that returns the character stream from a list of keystroke objects.
	 * 
	 * Does not handle backspaces or deletes.
	 * 
	 * @param keystrokes
	 * @return String corresponding to the keystrokes typed.
	 */
	private static String keyStrokesToString(Collection<KeyStroke> keystrokes) {
		String output = new String();
		for (KeyStroke k : keystrokes) {
			if (k.isKeyPress() && k.getKeyChar()!=KeyStroke.CHAR_UNDEFINED)
				output = output.concat( Character.toString(k.getKeyChar()) );
		}
		return output;
	}
	
	/**
	 * 
	 */
	private static boolean isNormalTrigraph (LinkedList<KeyStroke> buffer) {
		KeyStroke[] k = buffer.toArray(new KeyStroke[0]);
		// if key pattern follows p, r, p, r, p, r
		if (k[0].isKeyPress() && k[1].isKeyRelease() &&
			k[2].isKeyPress() && k[3].isKeyRelease() &&
			k[4].isKeyPress() && k[5].isKeyRelease()) {
			// if key pattern is A, A, B, B, C, C
			if (k[0].getKeyCode() == k[1].getKeyCode() &&
				k[2].getKeyCode() == k[3].getKeyCode() &&
				k[4].getKeyCode() == k[5].getKeyCode()) {
				return true;
			}
		}
		return false;
	}
}
