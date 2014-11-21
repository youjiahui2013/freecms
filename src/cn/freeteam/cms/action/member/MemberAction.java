package cn.freeteam.cms.action.member;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Member;
import cn.freeteam.cms.model.Membergroup;
import cn.freeteam.cms.service.MemberService;
import cn.freeteam.cms.service.MembergroupService;
import cn.freeteam.model.Users;
import cn.freeteam.util.FileUtil;
import cn.freeteam.util.MD5;
import cn.freeteam.util.OperLogUtil;
/**
 * 
 * <p>Title: MemberAction.java</p>
 * 
 * <p>Description:会员相关操作 </p>
 * 
 * <p>Date: Feb 1, 2013</p>
 * 
 * <p>Time: 8:00:46 PM</p>
 * 
 * <p>Copyright: 2013</p>
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
public class MemberAction extends BaseAction{

	private MemberService memberService;
	private MembergroupService membergroupService;
	private Member member;
	private Membergroup membergroup;
	private File img;
	private String imgFileName;
	private String oldImg;
	private String CurrentPassword="";
	private String NewPassword="";
	public MemberAction() {
		init("memberService");
	}
	/**
	 * 会员中心首页
	 * @return
	 */
	public String index(){
		return "index";
	}

	/**
	 * 会员中心左侧页面
	 * @return
	 */
	public String left(){
		return "left";
	}
	/**
	 * 个人资料修改
	 * @return
	 */
	public String profile(){
		try {
			if (getLoginMember()!=null) {
				//更新
				Member oldmember=getLoginMember();
				//如果原来有和现在的logo不同则删除原来的logo文件 
				if (!oldImg.equals(oldmember.getImg())) {
					if(oldmember.getImg()!=null &&  oldmember.getImg().trim().length()>0){
						FileUtil.del(getHttpRequest().getRealPath("/")+oldmember.getImg().trim());
						member.setImg("");
					}
				}else {
					member.setImg(oldImg);
				}
				if (img!=null) {
					//生成目标文件
					String root=getHttpRequest().getRealPath("/");
					String ext=FileUtil.getExt(imgFileName).toLowerCase();
					if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".gif".equals(ext) && !".png".equals(ext)) {
						write("<script>alert('头像只能上传jpg,jpeg,gif,png格式的图片!');history.back();</script>", "UTF-8");
						return null;
					}
					String id=UUID.randomUUID().toString();
					File targetFile=new File(root+"/upload/member/"+id+ext);
					File folder=new File(root+"/upload/member/");
					if (!folder.exists()) {
						folder.mkdirs();
					}
					if (!targetFile.exists()) {
						targetFile.createNewFile();
					}
					//复制到目标文件
					FileUtil.copy(img, targetFile);

					//生成访问地址
					member.setImg("/upload/member/"+id+ext);
				}
				member.setId(getLoginMember().getId());
				memberService.update(member);
				getHttpSession().setAttribute("loginMember", memberService.findByLoginname(getLoginMember()));
				OperLogUtil.log(getLoginMemberName(), "更新个人资料", getHttpRequest());
			}
			return showMessage("更新个人资料成功", "profile.jsp", 3);
		} catch (Exception e) {
			OperLogUtil.log(getLoginMemberName(), "更新个人资料失败:"+e.toString(), getHttpRequest());
			DBProException(e);
			return showMessage("更新个人资料失败:"+e.toString(), "", 0);
		}
	}


	//修改密码
	public String pwd(){
		member=getLoginMember();
		try {
			//先判断原密码是否正确
			if (!MD5.MD5(CurrentPassword).equals(member.getPwd())) {
				showMessage="当前密码不正确!";
			}
			//如果新密码不等于旧密码时才修改，减少数据库操作
			else {
				if (!CurrentPassword.equals(NewPassword)) {
					member.setPwd(MD5.MD5(NewPassword));
					memberService.update(member);
					getHttpSession().setAttribute("loginMember", member);
				}
				showMessage="密码更新成功!";
			}
		} catch (Exception e) {
			DBProException(e);
			showMessage="密码更新失败:"+e.toString()+"!";
		}
		OperLogUtil.log(member.getLoginname(), showMessage, getHttpRequest());
		return showMessage(showMessage, "pwd.jsp", 3);
	}
	public MemberService getMemberService() {
		return memberService;
	}
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	public MembergroupService getMembergroupService() {
		return membergroupService;
	}
	public void setMembergroupService(MembergroupService membergroupService) {
		this.membergroupService = membergroupService;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public Membergroup getMembergroup() {
		return membergroup;
	}
	public void setMembergroup(Membergroup membergroup) {
		this.membergroup = membergroup;
	}
	public File getImg() {
		return img;
	}
	public void setImg(File img) {
		this.img = img;
	}
	public String getImgFileName() {
		return imgFileName;
	}
	public void setImgFileName(String imgFileName) {
		this.imgFileName = imgFileName;
	}
	public String getOldImg() {
		return oldImg;
	}
	public void setOldImg(String oldImg) {
		this.oldImg = oldImg;
	}
	public String getCurrentPassword() {
		return CurrentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		CurrentPassword = currentPassword;
	}
	public String getNewPassword() {
		return NewPassword;
	}
	public void setNewPassword(String newPassword) {
		NewPassword = newPassword;
	}
}
