package com.spring.learn.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.spring.learn.board.NoticeService;
import com.spring.learn.board.NoticeVO;
import com.spring.learn.common.Paging;

//@SessionAttributes : board라는 이름의 Model 있으면 session에 저장
//  단, 현재위치(클래스)에서만 유효
@Controller
@SessionAttributes("notice") 
public class NoticeController { 
	@Autowired
	private NoticeService noticeService;
	
	@ModelAttribute("conditionMap")
	public Map<String, String> searchConditionMap() {
		System.out.println("=====> Map searchConditionMap() 실행");
		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("제목", "TITLE");
		conditionMap.put("내용", "CONTENT");
		
		return conditionMap;
	}
	
	@RequestMapping("/getNotice.do")
	public String getNotice(NoticeVO vo, Model model) {
		System.out.println(">> 게시글 상세 보여주기");
		
		NoticeVO vo2 = noticeService.getNotice(vo); 

		model.addAttribute("vo", vo2);
		
		return "Member/MemberBoard/noticeView.jsp";
	}
	
	// 검색과 페이지 보기, 페이징- 아작스 버전 하나 만들기
	@RequestMapping("/getNoticeList.do")
	public String getQnaBoardList(NoticeVO vo, Model model, Paging p, HttpSession session) {
		p.setNumPerPage(10);
		p.setNumPerBlock(10);
		
		// 전체 페이지 수 구하기
		p.setTotalRecord(noticeService.countBoard(vo));
		p.setTotalPage();
		
		// 현재 페이지 구하기
		if (p.getcPage() != 0) {
			p.setNowPage(p.getcPage());
		}
		
		// 현재 페이지에 시작할 첫게시글 번호, 끝 게시글 번호
		p.setEnd(p.getNowPage()*p.getNumPerPage());
		p.setBegin(p.getEnd() - p.getNumPerPage() +1);
		
		// 끝번호가 더 크면 토탈번호와 맞게 하기 - 끝블록 끝페이지 때문
		if (p.getEnd() > p.getTotalRecord()) p.setEnd(p.getTotalRecord());
		
		// 블록 계산하기
		int nowPage = p.getNowPage();
		int beginPage = (nowPage -1) / p.getNumPerBlock() * p.getNumPerBlock() + 1;
		p.setBeginPage(beginPage);
		p.setEndPage(beginPage + p.getNumPerBlock() - 1);
		
		if (p.getEndPage() > p.getTotalPage()) p.setEndPage(p.getTotalPage());		
		
		System.out.println("계산된 paging : " + p);
		
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("searchKeyword", vo.getSearchKeyword());
		map.put("begin", Integer.toString(p.getBegin()));
		map.put("end", Integer.toString(p.getEnd()));
		
		System.out.println(">>>>> map : " + map);

		// 리스트 가져오기
		List<NoticeVO> list = noticeService.getBoardList(map); // 조회하고

		// 리스트 모델에 저장
		if (list != null) {
			model.addAttribute("list", list); // 값 저장하고
			model.addAttribute("pvo", p);
		}		
		
		System.out.println("list : " + list);

		return "/Member/MemberBoard/notice.jsp"; // 이동
	}

//	@RequestMapping("/getJsonNoticeList.do")
//	@ResponseBody //response 응답객체의 몸체(body)에 데이터 전달
//	public List<NoticeVO> getJsonBoardList(NoticeVO vo, Model model, Criteria cri) {
//		System.out.println("========= NoticeController getJsonNoticeList() 실행");
//		List<NoticeVO> list = noticeService.getNoticeList(vo);
//		
//		//개발자참고용
//		for (int i = 0; i < list.size(); i++ ) {
//			System.out.println("controller noticeList 제목( " + i + ") : " + list.get(i).getBoardTitle());	
//		}	
//		
//		PageMaker pageMaker = new PageMaker();
//		pageMaker.setCri(cri);
//		pageMaker.setTotalCount(noticeService.listCount());
//		pageMaker.setEndPage(1); //나중에 수정하기
//		
//		model.addAttribute("pageMaker", pageMaker);
//		
//		System.out.println(">>>> getJsonNoticeList pageMaker : " + pageMaker);
//		
//		return list;
//	}


//	@RequestMapping("/getJsonNoticePage.do")
//	@ResponseBody //response 응답객체의 몸체(body)에 데이터 전달
//	public PageMaker getJsonBoardPage(NoticeVO vo, Model model, Criteria cri) {
//		System.out.println("========= NoticeController getJsonBoardPage() 실행");
//		//logger.info("list");
//
//		PageMaker pageMaker = new PageMaker();
//		pageMaker.setCri(cri);
//		pageMaker.setTotalCount(noticeService.searchListCount(vo));
//		pageMaker.setEndPage(1); //나중에 수정하기
//		
//		System.out.println("> getJsonNoticePage pageMaker : " + pageMaker);
//		
//		return pageMaker;
//	}
	
	
//	@RequestMapping("/getNoticeList.do")
//	public String list(Model model, Criteria cri) throws Exception{
//		System.out.println(">> 페이지에 해당하는 게시글 리스트 보여주기");
//		//logger.info("list");
//		
//		model.addAttribute("list", noticeService.list(cri));
//		
//		PageMaker pageMaker = new PageMaker();
//		pageMaker.setCri(cri);
//		pageMaker.setTotalCount(noticeService.listCount());
//		
//		model.addAttribute("pageMaker", pageMaker);
//		
//		System.out.println(">> getNoticeList pageMaker : " + pageMaker);
//		
//		return "Member/MemberBoard/notice.jsp";
//		
//	}	
	
	
/*	
	@RequestMapping("/getJsonNoticeList.do")
	@ResponseBody //response 응답객체의 몸체(body)에 데이터 전달
	public List<NoticeVO> getJsonBoardList(NoticeVO vo, Model model, Criteria cri) {
		System.out.println("========= NoticeController getJsonNoticeList() 실행");
		
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(noticeService.searchListCount(vo));
		
		List<NoticeVO> list = noticeService.getNoticeList(vo);
		System.out.println("Search Notice List : " + list);
		
		return list;
	}
*/	
	
/*
	@RequestMapping("/getJsonNoticeList.do")
	@ResponseBody //response 응답객체의 몸체(body)에 데이터 전달
	public List<NoticeVO> getJsonBoardList(NoticeVO vo) {
		System.out.println("========= NoticeController getJsonNoticeList() 실행");
		List<NoticeVO> list = noticeService.getNoticeList(vo);
		System.out.println("noticeList : " + list);
		
		return list;
	}
*/

/*
	//JSON 데이터를 받아 게시글 찾아서 JSON 데이터로 리턴(응답)
	@PostMapping("/getJsonBoard.do")
	public NoticeVO getJsonBoard(@RequestBody NoticeVO vo) { //@RequestBody post방식 전달데이터 처리
		System.out.println("========== BoardAjaxController getJsonBoard(vo) 실행");
		System.out.println("getJsonBoard() vo : " + vo);
		
		NoticeVO board = noticeService.getNotice(vo);
		System.out.println("getJsonBoard() notice : " + board);
		
		return board;
	}
*/
	
/*
	@RequestMapping("/getNoticeList.do")
	public String getNoticeList(NoticeVO vo, Model model) {

		List<NoticeVO> list = noticeService.getNoticeList(vo);
		
		model.addAttribute("list", list);
		
		return "Member/MemberBoard/notice.jsp";
	}
*/
	
	// 메소드의 정의부에 선언된 @ModelAttribute 는 리턴된 데이터를 View에 전달
	// @ModelAttribute 선언된 메소드는 @RequestMapping 메소드보다 먼저 실행
	// 뷰(View)에 전달될 때 설정된 명칭 사용(예: conditionMap)
/*
	@ModelAttribute("conditionMap")
	public Map<String, String> searchConditionMap() {
		System.out.println("=======> Map searchConditionMap() 실행");
		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("제목", "TITLE");
		conditionMap.put("내용", "CONTENT");
		
		return conditionMap;
	}
*/

}
