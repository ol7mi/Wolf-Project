package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import board.dao.BoardDAO;
import board.dto.BoardDTO;
import commons.PageConfig;
import files.dao.FilesDAO;
import files.dto.FilesDTO;
import reply.dao.ReplyDAO;
import reply_child.dao.Reply_childDAO;

@WebServlet("*.board")
public class BoardController extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		HttpSession session= request.getSession();
		String cmd = request.getRequestURI();
		
		BoardDAO boardDAO= BoardDAO.getInstance();
		FilesDAO filesDAO= FilesDAO.getInstance();
		ReplyDAO replyDAO= ReplyDAO.getInstance();
		Reply_childDAO reply_childDAO= Reply_childDAO.getInstance();
		List<BoardDTO> list = new ArrayList<>();
		
		try {
			if(cmd.equals("/list.board")) {
				String login_id= (String)session.getAttribute("WolfID");
	
				if(login_id==null) {
					System.out.println("로그인을 해주세요");
					response.sendRedirect("/index.jsp");
				}
				String target=request.getParameter("target");
				String keyword=request.getParameter("keyword");
				Object boardList[]=new Object[2];
				String pcpage=request.getParameter("cpage");
				if(pcpage==null) pcpage="1";
				int cpage=Integer.parseInt(pcpage);
		
				if(target==null||keyword.equals("")||target.equals("")) {
					boardList = boardDAO.selectAll( PageConfig.recordCountPerPage, cpage,PageConfig.board);
					request.setAttribute("record_total_count", boardDAO.getRecordCount("",""));
					
				}
				else {
					boardList = boardDAO.selectType( PageConfig.recordCountPerPage, cpage,PageConfig.board,target,keyword);
					System.out.println(	((List<BoardDTO>)boardList[0]).size());
					request.setAttribute("record_total_count", boardDAO.getRecordCount(target,keyword));
				}
			
				
				request.setAttribute("target",target);
				request.setAttribute("keyword",keyword);
				request.setAttribute("list", boardList[0]);
				request.setAttribute("board_nickname_list", boardList[1]);//boardList[1]
				request.setAttribute("cpage", cpage);
				request.setAttribute("record_count_per_page", PageConfig.recordCountPerPage);
				request.setAttribute("navi_count_per_page", PageConfig.naviCountPerPage);
				
				
				request.getRequestDispatcher("/views/board/board_view.jsp").forward(request, response);
				
			} else if(cmd.equals("/detail.board")) {
				int seq= Integer.parseInt(request.getParameter("seq"));
				boardDAO.countUp(seq);
				Object boardList[] =boardDAO.selectBoard(seq,PageConfig.board);
				Object replyList[] =replyDAO.select(seq);
				Object reply_childList[]=reply_childDAO.selectAll();
				List<FilesDTO> fileList=filesDAO.select(seq);
				
				System.out.println();
				
				
				
				request.setAttribute("board_dto", boardList[0]);
				request.setAttribute("board_nickname", boardList[1]);
				
				request.setAttribute("reply_list", replyList[0]);
				request.setAttribute("reply_nickname_list", replyList[1]);
			
				request.setAttribute("files_list", fileList);
				
				request.setAttribute("reply_child_list",reply_childList[0] );
				request.setAttribute("reply_child_nickname",reply_childList[1] );
				
				request.getRequestDispatcher("/views/board/board_detail.jsp").forward(request, response);
				
			} else if(cmd.equals("/insert.board")) {
				//session.setAttribute("WolfID", "test1");
				int maxSize = 1024 * 1024 * 10; // 10mb
				String realPath = request.getServletContext().getRealPath("files");
				File uploadPath = new File(realPath);
				if (!uploadPath.exists()) {
					uploadPath.mkdir();// 메이크 디렉토리
				}
				MultipartRequest multi = new MultipartRequest(request, realPath, maxSize, "UTF8",
						new DefaultFileRenamePolicy());

				String title=multi.getParameter("title");
				String contents=multi.getParameter("contents");
				String member_id= (String)session.getAttribute("WolfID");
				BoardDTO dto=new BoardDTO(0,title,contents,0,member_id,PageConfig.board,null);
				int board_seq= boardDAO.insert(dto);
				//dao_files.insert(new FilesDTO(0, oriName, sysName, parent_seq));
				Enumeration<String> names = multi.getFileNames();
		        while(names.hasMoreElements()) {
		               String name = names.nextElement();
		               String oriname = multi.getOriginalFileName(name);
		               String sysname = multi.getFilesystemName(name);
		               System.out.println(name);
		               
		               if(oriname != null) {
		            	   filesDAO.insert(new FilesDTO(0, oriname, sysname, board_seq));
		               }
		        }		
				response.sendRedirect("/list.board");
				
			} else if(cmd.equals("/delete.board")) {
				int seq=Integer.parseInt(request.getParameter("seq"));
				replyDAO.delete(seq);
				filesDAO.deleteAll(seq);
				boardDAO.delete(seq,PageConfig.board);
				response.sendRedirect("/list.board");
				
			} else if(cmd.equals("/update.board")) {
				//session.setAttribute("WolfID", "test1");
				
				int maxSize = 1024 * 1024 * 10; // 10mb
				String realPath = request.getServletContext().getRealPath("files");
				File uploadPath = new File(realPath);
				if (!uploadPath.exists()) {
					uploadPath.mkdir();// 메이크 디렉토리
				}
				MultipartRequest multi = new MultipartRequest(request, realPath, maxSize, "UTF8",
						new DefaultFileRenamePolicy());
				
				String array=multi.getParameter("array");
				Gson gson=new Gson();
				int[] intArray = gson.fromJson(array, int[].class);
				for (int i : intArray) {
					filesDAO.delete(i);
				}
				int seq=Integer.parseInt(multi.getParameter("seq"));
				String title =multi.getParameter("title");
				String contents=multi.getParameter("contents");
				String member_id= (String)session.getAttribute("WolfID");
				int count =Integer.parseInt(multi.getParameter("count"));
				boardDAO.update(new BoardDTO(seq,title,contents,count,member_id,PageConfig.board,null));
				Enumeration<String> names = multi.getFileNames();
		        while(names.hasMoreElements()) {
		               String name = names.nextElement();
		               String oriname = multi.getOriginalFileName(name);
		               String sysname = multi.getFilesystemName(name);
		               System.out.println(name);
		               
		               if(oriname != null) {
		            	   filesDAO.insert(new FilesDTO(0, oriname, sysname, seq));
		               }
		        }
				
				
				response.sendRedirect("/detail.board?seq="+seq);
				
			} else if(cmd.equals("/serch.board")) {
				
				
				Object boardList[]=new Object[2];
				String traget=request.getParameter("target");
				String keyword=request.getParameter("keyword");

				String pcpage=request.getParameter("cpage");
				if(pcpage==null) pcpage="1";
				int cpage=Integer.parseInt(pcpage);
				
				if(keyword.equals("")||traget.equals("")) {
					boardList = boardDAO.selectAll( PageConfig.recordCountPerPage, cpage,PageConfig.board);
					request.setAttribute("record_total_count", boardDAO.getRecordCount("",""));
				}
				else {
					boardList = boardDAO.selectType( PageConfig.recordCountPerPage, cpage,PageConfig.board,traget,keyword);
					System.out.println(	((List<BoardDTO>)boardList[0]).size());
					request.setAttribute("record_total_count", boardDAO.getRecordCount(traget,keyword));
				}
				
				
				request.setAttribute("list", boardList[0]);
				request.setAttribute("board_nickname_list", boardList[1]);//boardList[1]
				request.setAttribute("cpage", cpage);
				request.setAttribute("record_count_per_page", PageConfig.recordCountPerPage);
				request.setAttribute("navi_count_per_page", PageConfig.naviCountPerPage);
				request.getRequestDispatcher("/views/board/board_view.jsp").forward(request, response);
				
				
				
				
			}
			
		} catch (Exception e) {
		
		}
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
