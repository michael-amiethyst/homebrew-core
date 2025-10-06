class Bashpile < Formula
  desc "The Bash Transpiler: Write in a modern language and run in a Bash5 shell!"
  version "0.13.0"
  homepage "https://github.com/michael-amiethyst/homebrew-core"
  license "MIT"
  url "https://github.com/michael-amiethyst/homebrew-core", using: :git, branch: "main", tag: "0.16.0"
  head "https://github.com/michael-amiethyst/homebrew-core", using: :git, branch: "development"

  # foundational dependencies
  depends_on "openjdk"
  depends_on "bash"
  depends_on "make" => :build
  depends_on "gradle" => :build

  # tooling dependencies for compilation
  # depends_on "shfmt"
  # depends_on "shellcheck"

  # tooling dependencies for generated scripts
  depends_on "gnu-sed"
  depends_on "bc"
  # depends_on "gnu-getopt" # needed for OSX and FreeBSD, kept as generic dependency for consistency

  def install
    system "make", "jar"
    bin.install "build/bashpile"
  end

  test do
    assert_match "6.28", shell_output("bashpile -c \"print(3.14 + 3.14)\"")
  end

  def caveats
    <<~EOS
      Ensure the installed Bash is at least version 5 and is the default Bash.
    EOS
  end
end
