package cn.freeteam.cms.action;

import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Comment;
import cn.freeteam.cms.model.Guestbook;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.model.Visit;
import cn.freeteam.cms.service.CommentService;
import cn.freeteam.cms.service.GuestbookService;
import cn.freeteam.cms.service.InfoService;
import cn.freeteam.cms.service.VisitService;
import cn.freeteam.util.Pager;

/**
 * 
 * <p>Title: StatAction.java</p>
 * 
 * <p>Description:关于站点的统计分析 </p>
 * 
 * <p>Date: Jan 21, 2012</p>
 * 
 * <p>Time: 2:30:58 PM</p>
 * 
 * <p>Copyright: 2012</p>
 * 
 * <p>Company: freeteam</p>
 * 
 * @author freeteam
 * @version 1.0
 * 
 * <p>============================================</p>
 * <p>Modification History
 * <p>Mender: </p>
 * <p>Date: </p>
 * <p>Reason: </p>
 * <p>============================================</p>
 */
public class StatAction extends BaseAction{

	private InfoService infoService;
	private VisitService visitService;
	private GuestbookService guestbookService;
	private CommentService commentService;
	private List<Info> infoList;
	private List<Visit> visitList;
	private List<Guestbook> guestbookList;
	private List<Comment> commentList;
	private Comment comment;
	private Info info;
	private Visit visit;
	private Guestbook guestbook;
	private String export;
	private String statType;
	private Map<String, String> objtypes;
	private int sum;
	public StatAction() {
		init("infoService","visitService");
	}
	/**
	 * 工作量统计
	 * @return
	 */
	public String workload(){
		if (info==null) {
			info=new Info();
		}
		info.setSite(getManageSite().getId());
		sum=infoService.workloadSum(info);
		if ("1".equals(export)) {
			infoList=infoService.workload(info);
			return "workloadExport";
		}else {
			infoList=infoService.workload(info, currPage, pageSize);
			totalCount=infoService.workloadCount(info);
			return "workload";
		}
	}

	/**
	 * 系统 工作量统计
	 * @return
	 */
	public String sysWorkload(){
		if (info==null) {
			info=new Info();
		}
		sum=infoService.workloadSum(info);
		if ("1".equals(export)) {
			infoList=infoService.workload(info);
			return "sysWorkloadExport";
		}else {
			infoList=infoService.workload(info, currPage, pageSize);
			totalCount=infoService.workloadCount(info);
			return "sysWorkload";
		}
	}
	/**
	 * 系统 站点内容统计 
	 * @return
	 */
	public String sysSiteStat(){
		if (info==null) {
			info=new Info();
		}
		sum=infoService.siteStatSum(info);
		if ("1".equals(export)) {
			infoList=infoService.siteStat(info);
			return "sysSiteStatExport";
		}else {
			infoList=infoService.siteStat(info, currPage, pageSize);
			totalCount=infoService.siteStatCount(info);
			return "sysSiteStat";
		}
	}
	/**
	 * 栏目信息统计 
	 * @return
	 */
	public String channelStat(){
		if (info==null) {
			info=new Info();
		}
		info.setInfosite(getManageSite().getId());
		infoList=infoService.channelStat(info);
		sum=infoService.channelStatSum(info);
		if ("1".equals(export)) {
			return "channelStatExport";
		}else {
			totalCount=infoService.channelStatCount(info);
			return "channelStat";
		}
	}

	/**
	 * 信息更新统计 
	 * @return
	 */
	public String infoUpdate(){
		if (info==null) {
			info=new Info();
		}
		info.setInfosite(getManageSite().getId());
		if ("year".equals(statType)) {
			//按年统计
			sum=infoService.infoUpdateYearSum(info);
			if ("1".equals(export)) {
				infoList=infoService.infoUpdateYear(info);
				return "infoUpdateExport";
			}else {
				infoList=infoService.infoUpdateYear(info, currPage, pageSize);
				totalCount=infoService.infoUpdateYearCount(info);
				return "infoUpdate";
			}
		}
		else if ("month".equals(statType)) {
			//按月统计
			sum=infoService.infoUpdateMonthSum(info);
			if ("1".equals(export)) {
				infoList=infoService.infoUpdateMonth(info);
				return "infoUpdateExport";
			}else {
				infoList=infoService.infoUpdateMonth(info, currPage, pageSize);
				totalCount=infoService.infoUpdateMonthCount(info);
				return "infoUpdate";
			}
		}
		else if ("day".equals(statType)) {
			//按日统计
			sum=infoService.infoUpdateDaySum(info);
			if ("1".equals(export)) {
				infoList=infoService.infoUpdateDay(info);
				return "infoUpdateExport";
			}else {
				infoList=infoService.infoUpdateDay(info, currPage, pageSize);
				totalCount=infoService.infoUpdateDayCount(info);
				return "infoUpdate";
			}
		}
		else if ("week".equals(statType)) {
			//按周统计
			sum=infoService.infoUpdateWeekSum(info);
			infoList=infoService.infoUpdateWeek(info);
			if ("1".equals(export)) {
				return "infoUpdateExport";
			}else {
				return "infoUpdate";
			}
		}
		return "infoUpdate";
	}

	/**
	 * 系统信息更新统计 
	 * @return
	 */
	public String sysInfoUpdate(){
		if (info==null) {
			info=new Info();
		}
		if ("year".equals(statType)) {
			//按年统计
			sum=infoService.infoUpdateYearSum(info);
			if ("1".equals(export)) {
				infoList=infoService.infoUpdateYear(info);
				return "sysInfoUpdateExport";
			}else {
				infoList=infoService.infoUpdateYear(info, currPage, pageSize);
				totalCount=infoService.infoUpdateYearCount(info);
				return "sysInfoUpdate";
			}
		}
		else if ("month".equals(statType)) {
			//按月统计
			sum=infoService.infoUpdateMonthSum(info);
			if ("1".equals(export)) {
				infoList=infoService.infoUpdateMonth(info);
				return "sysInfoUpdateExport";
			}else {
				infoList=infoService.infoUpdateMonth(info, currPage, pageSize);
				totalCount=infoService.infoUpdateMonthCount(info);
				return "sysInfoUpdate";
			}
		}
		else if ("day".equals(statType)) {
			//按日统计
			sum=infoService.infoUpdateDaySum(info);
			if ("1".equals(export)) {
				infoList=infoService.infoUpdateDay(info);
				return "sysInfoUpdateExport";
			}else {
				infoList=infoService.infoUpdateDay(info, currPage, pageSize);
				totalCount=infoService.infoUpdateDayCount(info);
				return "sysInfoUpdate";
			}
		}
		else if ("week".equals(statType)) {
			//按周统计
			sum=infoService.infoUpdateWeekSum(info);
			infoList=infoService.infoUpdateWeek(info);
			if ("1".equals(export)) {
				return "sysInfoUpdateExport";
			}else {
				return "sysInfoUpdate";
			}
		}
		return "sysInfoUpdate";
	}
	/**
	 * 栏目访问统计
	 * @return
	 */
	public String channelVisit(){
		if (visit==null) {
			visit=new Visit();
		}
		visit.setStatType("channel");
		visit.setSiteid(getManageSite().getId());
		sum=visitService.channelVisitSum(visit);
		if ("1".equals(export)) {
			visitList=visitService.channelVisit(visit);
			return "channelVisitExport";
		}else {
			visitList=visitService.channelVisit(visit, currPage, pageSize);
			totalCount=visitService.channelVisitCount(visit);
			return "channelVisit";
		}
	}
	/**
	 * 系统 站点访问统计 
	 * @return
	 */
	public String sysSiteVisit(){
		if (visit==null) {
			visit=new Visit();
		}
		visit.setStatType("site");
		sum=visitService.siteVisitSum(visit);
		if ("1".equals(export)) {
			visitList=visitService.siteVisit(visit);
			return "sysSiteVisitExport";
		}else {
			visitList=visitService.siteVisit(visit, currPage, pageSize);
			totalCount=visitService.siteVisitCount(visit);
			return "sysSiteVisit";
		}
	}

	/**
	 * 信息访问统计
	 * @return
	 */
	public String infoVisit(){
		if (visit==null) {
			visit=new Visit();
		}
		visit.setStatType("info");
		visit.setSiteid(getManageSite().getId());
		sum=visitService.infoVisitSum(visit);
		if ("1".equals(export)) {
			visitList=visitService.infoVisit(visit);
			return "infoVisitExport";
		}else {
			visitList=visitService.infoVisit(visit, currPage, pageSize);
			totalCount=visitService.infoVisitCount(visit);
			return "infoVisit";
		}
	}

	/**
	 * 访问趋势统计 
	 * @return
	 */
	public String visit(){
		if (visit==null) {
			visit=new Visit();
		}
		visit.setSiteid(getManageSite().getId());
		if ("year".equals(statType)) {
			//按年统计
			sum=visitService.visitSum(visit);
			if ("1".equals(export)) {
				visitList=visitService.visitYear(visit);
				return "visitStatExport";
			}else {
				visitList=visitService.visitYear(visit, currPage, pageSize);
				totalCount=visitService.visitYearCount(visit);
				return "visitStat";
			}
		}
		else if ("month".equals(statType)) {
			//按月统计
			sum=visitService.visitSum(visit);
			if ("1".equals(export)) {
				visitList=visitService.visitMonth(visit);
				return "visitStatExport";
			}else {
				visitList=visitService.visitMonth(visit, currPage, pageSize);
				totalCount=visitService.visitMonthCount(visit);
				return "visitStat";
			}
		}
		else if ("day".equals(statType)) {
			//按日统计
			sum=visitService.visitSum(visit);
			if ("1".equals(export)) {
				visitList=visitService.visitDay(visit);
				return "visitStatExport";
			}else {
				visitList=visitService.visitDay(visit, currPage, pageSize);
				totalCount=visitService.visitDayCount(visit);
				return "visitStat";
			}
		}
		else if ("week".equals(statType)) {
			//按周统计
			sum=visitService.visitSum(visit);
			visitList=visitService.visitWeek(visit);
			if ("1".equals(export)) {
				return "visitStatExport";
			}else {
				return "visitStat";
			}
		}
		return "visitStat";
	}
	

	/**
	 * 访问趋势统计 系统
	 * @return
	 */
	public String sysVisit(){
		if (visit==null) {
			visit=new Visit();
		}
		if ("year".equals(statType)) {
			//按年统计
			sum=visitService.visitSum(visit);
			if ("1".equals(export)) {
				visitList=visitService.visitYear(visit);
				return "sysVisitStatExport";
			}else {
				visitList=visitService.visitYear(visit, currPage, pageSize);
				totalCount=visitService.visitYearCount(visit);
				return "sysVisitStat";
			}
		}
		else if ("month".equals(statType)) {
			//按月统计
			sum=visitService.visitSum(visit);
			if ("1".equals(export)) {
				visitList=visitService.visitMonth(visit);
				return "sysVisitStatExport";
			}else {
				visitList=visitService.visitMonth(visit, currPage, pageSize);
				totalCount=visitService.visitMonthCount(visit);
				return "sysVisitStat";
			}
		}
		else if ("day".equals(statType)) {
			//按日统计
			sum=visitService.visitSum(visit);
			if ("1".equals(export)) {
				visitList=visitService.visitDay(visit);
				return "sysVisitStatExport";
			}else {
				visitList=visitService.visitDay(visit, currPage, pageSize);
				totalCount=visitService.visitDayCount(visit);
				return "sysVisitStat";
			}
		}
		else if ("week".equals(statType)) {
			//按周统计
			sum=visitService.visitSum(visit);
			visitList=visitService.visitWeek(visit);
			if ("1".equals(export)) {
				return "sysVisitStatExport";
			}else {
				return "sysVisitStat";
			}
		}
		return "sysVisitStat";
	}

	/**
	 * 留言频率统计 
	 * @return
	 */
	public String guestbookUpdate(){
		init("guestbookService");
		if (guestbook==null) {
			guestbook=new Guestbook();
		}
		guestbook.setSiteid(getManageSite().getId());
		if ("year".equals(statType)) {
			//按年统计
			sum=guestbookService.guestbookUpdateYearSum(guestbook);
			if ("1".equals(export)) {
				guestbookList=guestbookService.guestbookUpdateYear(guestbook);
				return "guestbookUpdateExport";
			}else {
				guestbookList=guestbookService.guestbookUpdateYear(guestbook, currPage, pageSize);
				totalCount=guestbookService.guestbookUpdateYearCount(guestbook);
				return "guestbookUpdate";
			}
		}
		else if ("month".equals(statType)) {
			//按月统计
			sum=guestbookService.guestbookUpdateMonthSum(guestbook);
			if ("1".equals(export)) {
				guestbookList=guestbookService.guestbookUpdateMonth(guestbook);
				return "guestbookUpdateExport";
			}else {
				guestbookList=guestbookService.guestbookUpdateMonth(guestbook, currPage, pageSize);
				totalCount=guestbookService.guestbookUpdateMonthCount(guestbook);
				return "guestbookUpdate";
			}
		}
		else if ("day".equals(statType)) {
			//按日统计
			sum=guestbookService.guestbookUpdateDaySum(guestbook);
			if ("1".equals(export)) {
				guestbookList=guestbookService.guestbookUpdateDay(guestbook);
				return "guestbookUpdateExport";
			}else {
				guestbookList=guestbookService.guestbookUpdateDay(guestbook, currPage, pageSize);
				totalCount=guestbookService.guestbookUpdateDayCount(guestbook);
				return "guestbookUpdate";
			}
		}
		else if ("week".equals(statType)) {
			//按周统计
			sum=guestbookService.guestbookUpdateWeekSum(guestbook);
			guestbookList=guestbookService.guestbookUpdateWeek(guestbook);
			if ("1".equals(export)) {
				return "guestbookUpdateExport";
			}else {
				return "guestbookUpdate";
			}
		}
		return "guestbookUpdate";
	}
	/**
	 * 留言频率统计 系统
	 * @return
	 */
	public String sysGuestbookUpdate(){
		init("guestbookService");
		if (guestbook==null) {
			guestbook=new Guestbook();
		}
		if ("year".equals(statType)) {
			//按年统计
			sum=guestbookService.guestbookUpdateYearSum(guestbook);
			if ("1".equals(export)) {
				guestbookList=guestbookService.guestbookUpdateYear(guestbook);
				return "sysGuestbookUpdateExport";
			}else {
				guestbookList=guestbookService.guestbookUpdateYear(guestbook, currPage, pageSize);
				totalCount=guestbookService.guestbookUpdateYearCount(guestbook);
				return "sysGuestbookUpdate";
			}
		}
		else if ("month".equals(statType)) {
			//按月统计
			sum=guestbookService.guestbookUpdateMonthSum(guestbook);
			if ("1".equals(export)) {
				guestbookList=guestbookService.guestbookUpdateMonth(guestbook);
				return "sysGuestbookUpdateExport";
			}else {
				guestbookList=guestbookService.guestbookUpdateMonth(guestbook, currPage, pageSize);
				totalCount=guestbookService.guestbookUpdateMonthCount(guestbook);
				return "sysGuestbookUpdate";
			}
		}
		else if ("day".equals(statType)) {
			//按日统计
			sum=guestbookService.guestbookUpdateDaySum(guestbook);
			if ("1".equals(export)) {
				guestbookList=guestbookService.guestbookUpdateDay(guestbook);
				return "sysGuestbookUpdateExport";
			}else {
				guestbookList=guestbookService.guestbookUpdateDay(guestbook, currPage, pageSize);
				totalCount=guestbookService.guestbookUpdateDayCount(guestbook);
				return "sysGuestbookUpdate";
			}
		}
		else if ("week".equals(statType)) {
			//按周统计
			sum=guestbookService.guestbookUpdateWeekSum(guestbook);
			guestbookList=guestbookService.guestbookUpdateWeek(guestbook);
			if ("1".equals(export)) {
				return "sysGuestbookUpdateExport";
			}else {
				return "sysGuestbookUpdate";
			}
		}
		return "sysGuestbookUpdate";
	}

	/**
	 * 系统 站点留言统计
	 * @return
	 */
	public String sysSiteGuestbook(){
		init("guestbookService");
		if (guestbook==null) {
			guestbook=new Guestbook();
		}
		sum=guestbookService.sysSiteGuestbookSum(guestbook);
		if ("1".equals(export)) {
			guestbookList=guestbookService.sysSiteGuestbook(guestbook);
			return "sysSiteGuestbookExport";
		}else {
			guestbookList=guestbookService.sysSiteGuestbook(guestbook, currPage, pageSize);
			totalCount=guestbookService.sysSiteGuestbookCount(guestbook);
			return "sysSiteGuestbook";
		}
	}
	

	/**
	 * 评论频率统计 
	 * @return
	 */
	public String commentUpdate(){
		init("commentService");
		if (comment==null) {
			comment=new Comment();
		}
		objtypes=comment.getObjtypes();
		comment.setSiteid(getManageSite().getId());
		if ("year".equals(statType)) {
			//按年统计
			sum=commentService.commentUpdateYearSum(comment);
			if ("1".equals(export)) {
				commentList=commentService.commentUpdateYear(comment);
				return "commentUpdateExport";
			}else {
				commentList=commentService.commentUpdateYear(comment, currPage, pageSize);
				totalCount=commentService.commentUpdateYearCount(comment);
				return "commentUpdate";
			}
		}
		else if ("month".equals(statType)) {
			//按月统计
			sum=commentService.commentUpdateMonthSum(comment);
			if ("1".equals(export)) {
				commentList=commentService.commentUpdateMonth(comment);
				return "commentUpdateExport";
			}else {
				commentList=commentService.commentUpdateMonth(comment, currPage, pageSize);
				totalCount=commentService.commentUpdateMonthCount(comment);
				return "commentUpdate";
			}
		}
		else if ("day".equals(statType)) {
			//按日统计
			sum=commentService.commentUpdateDaySum(comment);
			if ("1".equals(export)) {
				commentList=commentService.commentUpdateDay(comment);
				return "commentUpdateExport";
			}else {
				commentList=commentService.commentUpdateDay(comment, currPage, pageSize);
				totalCount=commentService.commentUpdateDayCount(comment);
				return "commentUpdate";
			}
		}
		else if ("week".equals(statType)) {
			//按周统计
			sum=commentService.commentUpdateWeekSum(comment);
			commentList=commentService.commentUpdateWeek(comment);
			if ("1".equals(export)) {
				return "commentUpdateExport";
			}else {
				return "commentUpdate";
			}
		}
		return "commentUpdate";
	}
	/**
	 * 评论频率统计 系统
	 * @return
	 */
	public String sysCommentUpdate(){
		init("commentService");
		if (comment==null) {
			comment=new Comment();
		}
		objtypes=comment.getObjtypes();
		if ("year".equals(statType)) {
			//按年统计
			sum=commentService.commentUpdateYearSum(comment);
			if ("1".equals(export)) {
				commentList=commentService.commentUpdateYear(comment);
				return "sysCommentUpdateExport";
			}else {
				commentList=commentService.commentUpdateYear(comment, currPage, pageSize);
				totalCount=commentService.commentUpdateYearCount(comment);
				return "sysCommentUpdate";
			}
		}
		else if ("month".equals(statType)) {
			//按月统计
			sum=commentService.commentUpdateMonthSum(comment);
			if ("1".equals(export)) {
				commentList=commentService.commentUpdateMonth(comment);
				return "sysCommentUpdateExport";
			}else {
				commentList=commentService.commentUpdateMonth(comment, currPage, pageSize);
				totalCount=commentService.commentUpdateMonthCount(comment);
				return "sysCommentUpdate";
			}
		}
		else if ("day".equals(statType)) {
			//按日统计
			sum=commentService.commentUpdateDaySum(comment);
			if ("1".equals(export)) {
				commentList=commentService.commentUpdateDay(comment);
				return "sysCommentUpdateExport";
			}else {
				commentList=commentService.commentUpdateDay(comment, currPage, pageSize);
				totalCount=commentService.commentUpdateDayCount(comment);
				return "sysCommentUpdate";
			}
		}
		else if ("week".equals(statType)) {
			//按周统计
			sum=commentService.commentUpdateWeekSum(comment);
			commentList=commentService.commentUpdateWeek(comment);
			if ("1".equals(export)) {
				return "sysCommentUpdateExport";
			}else {
				return "sysCommentUpdate";
			}
		}
		return "sysCommentUpdate";
	}

	/**
	 * 系统 站点评论统计
	 * @return
	 */
	public String sysSiteComment(){
		init("commentService");
		if (comment==null) {
			comment=new Comment();
		}
		objtypes=comment.getObjtypes();
		sum=commentService.sysSiteCommentSum(comment);
		if ("1".equals(export)) {
			commentList=commentService.sysSiteComment(comment);
			return "sysSiteCommentExport";
		}else {
			commentList=commentService.sysSiteComment(comment, currPage, pageSize);
			totalCount=commentService.sysSiteCommentCount(comment);
			return "sysSiteComment";
		}
	}
	//set and get
	public InfoService getInfoService() {
		return infoService;
	}
	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}
	public List<Info> getInfoList() {
		return infoList;
	}
	public void setInfoList(List<Info> infoList) {
		this.infoList = infoList;
	}
	public Info getInfo() {
		return info;
	}
	public void setInfo(Info info) {
		this.info = info;
	}
	public String getExport() {
		return export;
	}
	public void setExport(String export) {
		this.export = export;
	}
	public String getStatType() {
		return statType;
	}
	public void setStatType(String statType) {
		this.statType = statType;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public VisitService getVisitService() {
		return visitService;
	}
	public void setVisitService(VisitService visitService) {
		this.visitService = visitService;
	}
	public List<Visit> getVisitList() {
		return visitList;
	}
	public void setVisitList(List<Visit> visitList) {
		this.visitList = visitList;
	}
	public Visit getVisit() {
		return visit;
	}
	public void setVisit(Visit visit) {
		this.visit = visit;
	}
	public Guestbook getGuestbook() {
		return guestbook;
	}
	public void setGuestbook(Guestbook guestbook) {
		this.guestbook = guestbook;
	}
	public GuestbookService getGuestbookService() {
		return guestbookService;
	}
	public void setGuestbookService(GuestbookService guestbookService) {
		this.guestbookService = guestbookService;
	}
	public List<Guestbook> getGuestbookList() {
		return guestbookList;
	}
	public void setGuestbookList(List<Guestbook> guestbookList) {
		this.guestbookList = guestbookList;
	}
	public CommentService getCommentService() {
		return commentService;
	}
	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}
	public List<Comment> getCommentList() {
		return commentList;
	}
	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}
	public Comment getComment() {
		return comment;
	}
	public void setComment(Comment comment) {
		this.comment = comment;
	}
	public Map<String, String> getObjtypes() {
		return objtypes;
	}
	public void setObjtypes(Map<String, String> objtypes) {
		this.objtypes = objtypes;
	}
}
