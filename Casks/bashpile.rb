cask "Bashpile" do
  desc "The Bash Transpiler: Write in a modern language and run in a Bash5 shell!"
  homepage "https://github.com/michael-amiethyst/homebrew-core"
  license "MIT"
  url "https://github.com/michael-amiethyst/homebrew-core", using: :git, branch: "main", tag: "0.6.0"
  head "https://github.com/michael-amiethyst/homebrew-core", using: :git, branch: "feature/nativeLogging"

  # foundational dependencies
  depends_on "bash"
  depends_on "gradle" => :build

  # tooling dependencies for compilation
  # depends_on "shfmt"
  # depends_on "shellcheck"

  # tooling dependencies for generated scripts
  # depends_on "gnu-sed"
  # depends_on "bc"
  depends_on "gnu-getopt" # needed for OSX and FreeBSD, kept as generic dependency for consistency

  # TODO change to binary Stanza: https://docs.brew.sh/Cask-Cookbook#stanza-binary
  def install
    system "gradle", "clean", "nativeCompile", "-x", "test"
    bin.install "build/native/nativeCompile/bashpile"
    # bin.install "target/bpc"
    # FileUtils.cp "#{bin}/bpc", "#{bin}/bashpilec"
    # bin.install "target/bpr"
    # FileUtils.cp "#{bin}/bpr", "#{bin}/bashpile"
    # bin.install "target/bashpile-stdlib"
  end

  test do
    assert_match "Hello Bash", shell_output("printf \"print('Hello World')\" > /tmp/hello && bashpile /tmp/hello")
    # assert_match "6.28", shell_output("echo \"print(3.14 + 3.14)\" | bpr -c")
  end

  def caveats
    <<~EOS
      OSX Only: By default, the Bash5 and GNU-getopt dependencies will be installed to:
      	/usr/local/bash/bin
      	and
      	/usr/local/gnu-getopt/bin

        You will need to add /usr/local/gnu-getopt/bin to the front of your PATH for Bashpile to work correctly.
        You can add /usr/local/bash/bin to the front of your path as well, or set it to your default shell with `chsh`.
    EOS
  end
end
