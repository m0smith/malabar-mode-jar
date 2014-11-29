(setenv "GROOVY_HOME" "C:/Users/lpmsmith/.gvm/groovy/2.3.7")
(setq groovy-home "C:/Users/lpmsmith/.gvm/groovy/2.3.7")

(defun groovy-send-string (str)
  "Send the current region to the inferior Groovy process."
  (interactive "r")

  (save-excursion
    (save-restriction
      (let (
	    (proc (groovy-proc)))

      (with-current-buffer (process-buffer proc)
	(while (and
		(goto-char comint-last-input-end)
		(not (re-search-forward comint-prompt-regexp nil t))
		(accept-process-output proc)))
	(goto-char (process-mark proc))
	;(insert-before-markers str)
	(move-marker comint-last-input-end (point))
	(comint-send-string proc str)
	(comint-send-string proc "\n")
	)
      )
    )))

(groovy-send-string "this.getClass().classLoader.rootLoader.addURL(new File('c:/Users/lpmsmith/projects/malabar-mode-jar/build/libs/malabar-mode-jar.jar').toURL())
mp = new com.software_ninja.malabar.project.MavenProject();

")



(defun groovy-send-with-output (str)
  (interactive)
  
  (save-excursion
    (save-restriction
      (groovy-send-string str)
      (let* ((proc (groovy-proc)))
	  (with-current-buffer (process-buffer proc)
	    (let ((end (process-mark proc))
		  (start comint-last-input-end))
	      (goto-char start)
	      (re-search-forward str nil t)
	      (message "%s" (point))
	      (point)))))))


(groovy-send-with-output "mp.projectInfo('c:/Users/lpmsmith/.m2/repository', 'c:/Users/lpmsmith/projects/malabar-mode-jar/pom.xml');")
(switch-to-groovy t)


(defun xx (x)
  `(lambda (y) (+ ,x y)))
