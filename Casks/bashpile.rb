cask "bashpile" do
  version "0.7.0"
  url "https://github.com/michael-amiethyst/homebrew-core", using: :git, branch: "main", tag: "0.7.0"
  name "Bashpile"
  desc "The Bash Transpiler: Write in a modern language and run in a Bash5 shell!"
  homepage "https://github.com/michael-amiethyst/homebrew-core"
  # license "MIT"

  # foundational dependencies
  # depends_on "bash"

  # tooling dependencies for compilation
  # depends_on "shfmt"
  # depends_on "shellcheck"

  # tooling dependencies for generated scripts
  # depends_on "gnu-sed"
  # depends_on "bc"
  # depends_on "gnu-getopt" # needed for OSX and FreeBSD, kept as a generic dependency for consistency

  # TODO look into 'artifact, target' stanza for per-os binaries
  binary "bin/bashpile"

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
