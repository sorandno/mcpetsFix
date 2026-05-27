@echo off
echo --- Git管理からの削除を開始します ---

:: エラーが出ても無視して進む設定
git rm -r --cached .git/ 2>nul
git rm -r --cached .claude/ 2>nul
git rm -r --cached .gradle/ 2>nul
git rm -r --cached .idea/ 2>nul
git rm -r --cached build/ 2>nul

echo.
echo --- 作業完了 ---
echo エラーが出た場合は、すでに管理外なので問題ありません。
echo IntelliJに戻って「Commit and Push」をしてください。
echo.
pause
